package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import app.climbeyond.beyondcalendar.BeyondCalendar
import app.climbeyond.beyondcalendar.R
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.max

@SuppressLint("ViewConstructor")
class CalendarPager(val beyondCalendar: BeyondCalendar, context: Context,
        attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    private var selectedPage: Int = 0

    private val onPageChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectedPage = position

            getMonthViewForPosition(position)?.let { view ->
                view.setSelectedDate(beyondCalendar.selectedDate)
                beyondCalendar.onMonthSelected(getDateForPosition(position))
            }
        }
    }

    init {
        adapter = Adapter()
        addOnPageChangeListener(onPageChangeListener)

        currentItem = getPositionForDate(beyondCalendar.selectedDate)
    }

    private fun getDateForPosition(position: Int): ZonedDateTime =
        beyondCalendar.settings.monthFrom.plusMonths(position.toLong())

    private fun getPositionForDate(date: ZonedDateTime): Int =
        ChronoUnit.MONTHS.between(beyondCalendar.settings.monthFrom, date).toInt()

    private fun getMonthViewForPosition(position: Int): MonthLayout? = findViewWithTag(
            context.getString(R.string.month_view_tag_name, position)) as? MonthLayout

    internal fun getCurrentPageDate() : ZonedDateTime {
        return getDateForPosition(currentItem)
    }

    internal fun getMonthView(date: ZonedDateTime): MonthLayout? {
        return getMonthViewForPosition(getPositionForDate(date))
    }

    internal fun updateSelectedDate() {
        setCurrentItem(getPositionForDate(beyondCalendar.selectedDate), true)

        getMonthViewForPosition(currentItem)?.let {
            it.setSelectedDate(beyondCalendar.selectedDate)
            beyondCalendar.onMonthSelected(beyondCalendar.selectedDate)
        }
    }

    private inner class Adapter : PagerAdapter() {

        override fun notifyDataSetChanged() {
            super.notifyDataSetChanged()

            // Clear current adapter, recreate and set to the correct item when settings changed
            adapter = null
            adapter = Adapter()
            currentItem = getPositionForDate(beyondCalendar.selectedDate)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val datePos = getDateForPosition(position)
            val view = MonthLayout(context, beyondCalendar.settings, datePos, beyondCalendar.selectedDate).apply {
                tag = context.getString(R.string.month_view_tag_name, position)
                onDateSelected = { date ->
                    beyondCalendar.selectedDate = date
                    beyondCalendar.onDateSelected(date)
                }
            }

            container.addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))

            if (position == selectedPage) {
                beyondCalendar.onMonthSelected(datePos)
            }

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            (view as? View)?.let { container.removeView(it) }
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj

        override fun getCount(): Int = max(0, ChronoUnit.MONTHS.between(
                beyondCalendar.settings.monthFrom, beyondCalendar.settings.monthTo).toInt() + 1)
    }
}