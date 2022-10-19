package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import java.time.DayOfWeek
import java.util.*

@SuppressLint("ViewConstructor")
class MonthLegendCellGroup(context: Context, settings: BeyondCalendarSettings) :
        CellGroup(context, settings) {

    companion object {

        const val DEFAULT_DAYS_IN_WEEK = 7
    }

    override val rowNum: Int
        get() = 1
    override val colNum: Int
        get() = DEFAULT_DAYS_IN_WEEK

    private var dayOfWeekOffset: Int = -1

    init {
        updateLayout()
    }

    private fun updateLayout() {
        if (dayOfWeekOffset != settings.dayOfWeekOffset) {
            dayOfWeekOffset = settings.dayOfWeekOffset

            // remove all children
            removeAllViews()

            // populate children
            populateViews()
        }
    }

    private fun populateViews() {
        // add WeekDayViews in 7x1 grid
        getPermutation(dayOfWeekOffset).forEach { weekDay ->
            addView(MonthLegendCellView(context, settings, weekDay))
        }
    }

    /**
     * Get dayOfWeekOffset permutation for DayOfWeek
     */
    private fun getPermutation(n: Int): List<DayOfWeek> = DayOfWeek.values().let {
        val indices = it.size.let { size ->
            (0 until size).map { i ->
                (i + n) % size
            }
        }
        it.slice(indices)
    }
}
