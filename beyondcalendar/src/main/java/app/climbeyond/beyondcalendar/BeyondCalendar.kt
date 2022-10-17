package app.climbeyond.beyondcalendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import app.climbeyond.beyondcalendar.view.MonthLayout
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.max

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BeyondCalendar(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0, defStyleRes: Int = 0) : ViewPager(context, attrs) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    private var settings: BeyondCalendarSettings = BeyondCalendarSettings(context)
    private var selectedPage: Int = 0

    private val onPageChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectedPage = position

            getMonthViewForPosition(position)?.let { view ->
                view.setSelectedDate(lastSelectedDate)
                onMonthSelected?.invoke(getDateForPosition(position), view)
            }
        }
    }

    var lastSelectedDate: ZonedDateTime = ZonedDateTime.now(settings.timeZoneId)
        private set

    var monthCurrent: ZonedDateTime
        get() = getDateForPosition(currentItem)
        set(value) {
            currentItem = getPositionForDate(value)
        }

    // Default can scroll back one year
    var monthFrom: ZonedDateTime = ZonedDateTime.now().minusYears(1).withMonth(1).withDayOfMonth(1)
        set(value) {
            field = value
            adapter?.notifyDataSetChanged()
        }

    // default can not scroll to future
    var monthTo: ZonedDateTime = ZonedDateTime.now(settings.timeZoneId)
        set(value) {
            field = value
            adapter?.notifyDataSetChanged()
        }

    var firstDayOfWeek: DayOfWeek
        get() = settings.firstDayOfWeek
        set(value) {
            settings.firstDayOfWeek = value
        }

    var timeZone: TimeZone
        get() = settings.timeZone
        set(value) {
            settings.apply {
                timeZone = value
            }
        }

    var locale: Locale
        get() = settings.locale
        set(value) {
            settings.apply {
                locale = value
            }
        }

    var onMonthSelected: ((date: ZonedDateTime, view: MonthLayout) -> Unit)? = null
    var onDateSelected: ((date: ZonedDateTime) -> Unit)? = null

    init {
        val attr = context.obtainStyledAttributes(
                attrs, R.styleable.BeyondCalendar, defStyleAttr, defStyleRes)

        (0 until attr.indexCount).forEach { i ->
            when (val index = attr.getIndex(i)) {
                R.styleable.BeyondCalendar_calendarDateFrom -> {
                    monthFrom = ZonedDateTime.ofInstant(attr.getString(index)?.toLong()?.let {
                        Instant.ofEpochMilli(it)
                    }, settings.timeZoneId) ?: monthFrom
                }
                R.styleable.BeyondCalendar_calendarDateTo -> {
                    monthTo = ZonedDateTime.ofInstant(attr.getString(index)?.toLong()?.let {
                        Instant.ofEpochMilli(it)
                    }, settings.timeZoneId) ?: monthTo
                }
                R.styleable.BeyondCalendar_weekdayTextSize -> {
                    setWeekDayRawTextSize(attr.getDimension(index, 0f))
                }
                R.styleable.BeyondCalendar_dayTextSize -> {
                    setDayRawTextSize(attr.getDimension(index, 0f))
                }
                R.styleable.BeyondCalendar_dayTextColor -> {
                    setColorDayText(attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.day_text_color, context.theme)))
                }
                R.styleable.BeyondCalendar_dayTextSelectedColor -> {
                    setColorDaySelectedText(attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.day_text_selected_color, context.theme)))
                }
                R.styleable.BeyondCalendar_dayTextTodayColor -> {
                    setColorDayTodayText(attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.day_text_today_color, context.theme)))
                }
                R.styleable.BeyondCalendar_dayTextTodaySelectedColor -> {
                    setColorDayTodaySelectedText(attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.day_text_today_selected_color, context.theme)))
                }
                R.styleable.BeyondCalendar_weekdayTextColor -> {
                    setColorWeekdayText(attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.weekday_text_color, context.theme)))
                }
                R.styleable.BeyondCalendar_selectionColor -> {
                    attr.getColorStateList(index)?.let { setColorSelectionBackground(it) }
                }
                R.styleable.BeyondCalendar_accentColor -> {
                    attr.getColorStateList(index)?.let { setColorAccentDefault(it) }
                }
                R.styleable.BeyondCalendar_firstDayOfWeek -> {
                    firstDayOfWeek = DayOfWeek.values()[attr.getInt(index, 0).coerceIn(0..6)]
                }
            }
        }
        attr.recycle()

        adapter = Adapter()
        addOnPageChangeListener(onPageChangeListener)

        currentItem = getPositionForDate(ZonedDateTime.now(settings.timeZoneId))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec

        val specHeightSize = MeasureSpec.getSize(heightSpec)
        val specHeightMode = MeasureSpec.getMode(heightSpec)
        if (specHeightMode == MeasureSpec.AT_MOST || specHeightMode == MeasureSpec.UNSPECIFIED) {
            val height = childList.map {
                it.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(specHeightSize, specHeightMode))
                it.measuredHeight
            }.fold(0) { h1, h2 ->
                max(h1, h2)
            }
            heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    private fun getDateForPosition(position: Int): ZonedDateTime =
        monthFrom.plusMonths(position.toLong())

    private fun getPositionForDate(date: ZonedDateTime): Int =
        ChronoUnit.MONTHS.between(monthFrom, date).toInt()

    private fun getMonthViewForPosition(position: Int): MonthLayout? = findViewWithTag(
            context.getString(R.string.month_view_tag_name, position)) as? MonthLayout

    fun getCurrentMonthView(): MonthLayout? {
        return getMonthViewForPosition(currentItem)
    }

    fun setSelectedDate(date: ZonedDateTime) {
        getMonthViewForPosition(getPositionForDate(date))?.setSelectedDate(date)
    }

    fun setWeekDayTextSize(unit: Int, size: Float) {
        setWeekDayRawTextSize(
                TypedValue.applyDimension(unit, size, context.resources.displayMetrics))
    }

    fun setDayTextSize(unit: Int, size: Float) {
        setDayRawTextSize(TypedValue.applyDimension(unit, size, context.resources.displayMetrics))
    }

    /**
     * Day text setters
     */
    private fun setDayRawTextSize(size: Float) {
        settings.dayView.textSize = size
    }

    fun setColorDayText(color: Int) {
        settings.dayView.textColor = color
    }

    fun setColorDaySelectedText(color: Int) {
        settings.dayView.textColorSelected = color
    }

    fun setColorDayTodayText(color: Int) {
        settings.dayView.textColorToday = color
    }

    fun setColorDayTodaySelectedText(color: Int) {
        settings.dayView.textColorTodaySelected = color
    }

    fun setColorSelectionBackground(colorStateList: ColorStateList) {
        settings.dayView.setCircleColorStateList(colorStateList)
    }

    // Default Accent/DotAccent color if not defined when added
    fun setColorAccentDefault(colorStateList: ColorStateList) {
        settings.dayView.setAccentColorStateList(colorStateList)
    }

    // Sets color of a day number
    fun setColorDayFilter(weekday: DayOfWeek, color: Int) {
        settings.dayView.setTextFilterColor(weekday, color)
    }

    /**
     * Weekday text setters
     */
    fun setColorWeekdayText(color: Int) {
        settings.weekdayView.textColor = color
    }

    fun setColorWeekdayFilter(weekday: DayOfWeek, color: Int) {
        settings.weekdayView.setTextFilterColor(weekday, color)
    }

    private fun setWeekDayRawTextSize(size: Float) {
        settings.weekdayView.textSize = size
    }

    private inner class Adapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val view =
                MonthLayout(context, settings, getDateForPosition(position), lastSelectedDate).apply {
                    tag = context.getString(R.string.month_view_tag_name, position)
                    onDateSelected = { date ->
                        lastSelectedDate = date
                        this@BeyondCalendar.onDateSelected?.invoke(date)
                    }
                }
            container.addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT))

            if (position == selectedPage) {
                onMonthSelected?.invoke(getDateForPosition(position), view)
            }

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            (view as? View)?.let { container.removeView(it) }
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean = view === obj

        override fun getCount(): Int = max(0, ChronoUnit.MONTHS.between(monthFrom, monthTo).toInt() + 1)
    }
}
