package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import app.climbeyond.beyondcalendar.accent.Accent
import java.time.ZonedDateTime
import java.util.*

@Suppress("unused")
@SuppressLint("ViewConstructor")
class MonthLayout(context: Context, settings: BeyondCalendarSettings,
        private var month: ZonedDateTime,
        selectedDateInit: ZonedDateTime) : LinearLayout(context) {

    internal var onDateSelected: ((date: ZonedDateTime) -> Unit)? = null

    private val monthLegendCellGroup = MonthLegendCellGroup(context, settings)
    private val monthBodyCellGroup = MonthBodyCellGroup(context, settings, month, selectedDateInit).apply {
        onDateSelected = { date -> this@MonthLayout.onDateSelected?.invoke(date) }
    }

    init {
        orientation = VERTICAL
        addView(monthLegendCellGroup, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
        addView(monthBodyCellGroup, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun getSelectedDate(): Date? {
        return monthBodyCellGroup.selectedDayView?.date?.toInstant()?.let { Date.from(it) }
    }

    fun setSelectedDate(date: ZonedDateTime) {
        monthBodyCellGroup.setSelectedDay(date)
    }

    fun setAccents(date: ZonedDateTime, accents: Collection<Accent>) {
        monthBodyCellGroup.apply {
            getDayView(date)?.setAccents(accents)
        }.invalidateDayViews()
    }

    fun setAccents(map: Map<ZonedDateTime, Collection<Accent>>) {
        map.forEach {
            val (date, accents) = it
            monthBodyCellGroup.getDayView(date)?.setAccents(accents)
        }
        monthBodyCellGroup.invalidateDayViews()
    }

    override fun toString(): String = "MonthView($month)"
}
