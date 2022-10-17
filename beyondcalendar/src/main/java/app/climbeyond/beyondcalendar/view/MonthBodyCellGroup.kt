package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.view.ViewCompat
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import app.climbeyond.beyondcalendar.childList
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@SuppressLint("ViewConstructor")
class MonthBodyCellGroup(context: Context, settings: BeyondCalendarSettings,
        private var month: ZonedDateTime, selectedDate: ZonedDateTime) :
        CellGroup(context, settings) {

    companion object {

        const val DEFAULT_WEEKS = 6
        val DEFAULT_DAYS_IN_WEEK = DayOfWeek.values().size
    }

    override val rowNum: Int
        get() = DEFAULT_WEEKS
    override val colNum: Int
        get() = DEFAULT_DAYS_IN_WEEK

    internal var selectedDayView: MonthBodyCellDayView? = null
    internal var onDateSelected: ((date: ZonedDateTime) -> Unit)? = null

    // This is the first date in the view top left corner that can be previous month date
    private lateinit var viewFirstDate: ZonedDateTime
    private var dayOfWeekOffset: Int = -1
    private val thisMonth: Int = month.monthValue

    init {
        // update the layout
        updateLayout()

        setSelectedDay(selectedDate)
    }

    private fun updateLayout() {
        if (dayOfWeekOffset != settings.dayOfWeekOffset) {
            dayOfWeekOffset = settings.dayOfWeekOffset

            // calculate the date of top-left cell
            viewFirstDate = month.toInstant().atZone(settings.timeZone.toZoneId())
                .truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1).let {
                    val dayOfYearOffset = (-it.dayOfWeek.value + dayOfWeekOffset + 1).let { offset ->
                        if (offset > 0) (offset - DayOfWeek.values().size) else offset
                    }
                    it.plusDays(dayOfYearOffset.toLong())
                }

            // remove all children
            removeAllViews()

            // populate children with date including offset
            populateViews()
        }
    }

    private fun populateViews() {
        var cal = viewFirstDate

        (0 until rowNum).forEach { _ ->
            (0 until colNum).forEach { _ ->
                when (cal.monthValue) {
                    thisMonth -> {
                        addView(instantiateDayView(cal))
                    }
                    else -> {
                        addView(MonthBodyCellEmptyView(context, settings))
                    }
                }
                cal = cal.plusDays(1)
            }
        }
    }

    private fun instantiateDayView(cal: ZonedDateTime) =
        MonthBodyCellDayView(context, settings, cal).apply {
            setOnClickListener { setSelectedDay(this) }
        }

    internal fun invalidateDayViews() {
        childList.mapNotNull { it as? MonthBodyCellDayView }.forEach {
            it.updateState()
            ViewCompat.postInvalidateOnAnimation(it)
        }
    }

    fun setSelectedDay(date: ZonedDateTime) {
        setSelectedDay(getDayView(date))
    }

    private fun setSelectedDay(view: MonthBodyCellDayView?) {
        selectedDayView?.apply {
            isSelected = false
            updateState()
        }
        selectedDayView = view?.apply {
            isSelected = true
            updateState()
            onDateSelected?.invoke(date)
        }
    }

    fun getDayView(date: ZonedDateTime): MonthBodyCellDayView? {
        return childList.getOrNull(ChronoUnit.DAYS.between(
                viewFirstDate.toInstant(), date.toInstant()).toInt()) as? MonthBodyCellDayView
    }
}
