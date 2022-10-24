package app.climbeyond.beyondcalendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import app.climbeyond.beyondcalendar.accent.Accent
import app.climbeyond.beyondcalendar.view.CalendarHeader
import app.climbeyond.beyondcalendar.view.CalendarPager
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZonedDateTime
import kotlin.math.max

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BeyondCalendar(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0, defStyleRes: Int = 0) : LinearLayout(context, attrs) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    val settings: BeyondCalendarSettings = BeyondCalendarSettings(context, this)

    internal val calendarPager: CalendarPager
    internal var calendarHeader: CalendarHeader? = null

    var selectedDate: ZonedDateTime
        internal set

    var onMonthSelected: ((date: ZonedDateTime) -> Unit)? = null
    var onDateSelected: ((date: ZonedDateTime) -> Unit)? = null
    var onHeaderTodayClicked: (() -> Unit)? = null

    init {
        val attr = context.obtainStyledAttributes(
                attrs, R.styleable.BeyondCalendar, defStyleAttr, defStyleRes)

        (0 until attr.indexCount).forEach { i ->
            when (val index = attr.getIndex(i)) {
                R.styleable.BeyondCalendar_calendarDateFrom -> {
                    settings.monthFrom = ZonedDateTime.ofInstant(attr.getString(index)?.toLong()?.let {
                        Instant.ofEpochMilli(it)
                    }, settings.timeZoneId) ?: settings.monthFrom
                }
                R.styleable.BeyondCalendar_calendarDateTo -> {
                    settings.monthTo = ZonedDateTime.ofInstant(attr.getString(index)?.toLong()?.let {
                        Instant.ofEpochMilli(it)
                    }, settings.timeZoneId) ?: settings.monthTo
                }
                R.styleable.BeyondCalendar_weekdayTextSize -> {
                    settings.weekdayView.textSize = attr.getDimension(index, 0f)
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
                    settings.weekdayView.textColor = attr.getColor(index, ResourcesCompat.getColor(
                            resources, R.color.weekday_text_color, context.theme))
                }
                R.styleable.BeyondCalendar_selectionColor -> {
                    attr.getColorStateList(index)?.let { setColorSelectionBackground(it) }
                }
                R.styleable.BeyondCalendar_accentColor -> {
                    attr.getColorStateList(index)?.let { setColorAccentDefault(it) }
                }
                R.styleable.BeyondCalendar_firstDayOfWeek -> {
                    settings.firstDayOfWeek = DayOfWeek.values()[attr.getInt(index, 0).coerceIn(0..6)]
                }
                R.styleable.BeyondCalendar_headerVisible -> {
                    settings.calendarHeader.visible = attr.getBoolean(index, true)
                }
                R.styleable.BeyondCalendar_headerBgColor -> {
                    settings.calendarHeader.bgColor = attr.getColor(index, ResourcesCompat.getColor(
                            resources, android.R.color.darker_gray, context.theme))
                }
                R.styleable.BeyondCalendar_headerTextSize -> {
                    settings.calendarHeader.textSize = attr.getDimension(index,
                            context.resources.getDimension(R.dimen.header_text_size))
                }
                R.styleable.BeyondCalendar_headerTextColor -> {
                    settings.calendarHeader.textColor = attr.getColor(index, ResourcesCompat.getColor(
                            resources, android.R.color.white, context.theme))
                }
            }
        }
        attr.recycle()

        orientation = VERTICAL

        selectedDate = ZonedDateTime.now(settings.timeZoneId)

        // First create pager that is needed in CalendarHeader
        calendarPager = CalendarPager(this, context, attrs)

        if (settings.calendarHeader.visible) {
            calendarHeader = CalendarHeader(this, context)
            addView(calendarHeader, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        }

        addView(calendarPager, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
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

    fun setAccents(month: ZonedDateTime, accents: Map<ZonedDateTime, Collection<Accent>>) {
        calendarPager.getMonthView(month)?.setAccents(accents)
    }

    fun setWeekDayTextSize(unit: Int, size: Float) {
        settings.weekdayView.textSize = TypedValue.applyDimension(
                unit, size, context.resources.displayMetrics)
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

    internal fun onMonthSelected(date: ZonedDateTime) {
        calendarHeader?.updateHeaderText()
        onMonthSelected?.invoke(date)
    }

    internal fun onDateSelected(date: ZonedDateTime) {
        onDateSelected?.invoke(date)
    }

    internal fun onHeaderTodayClicked() {
        selectedDate = ZonedDateTime.now()
        calendarPager.updateSelectedDate()

        onHeaderTodayClicked?.invoke()
    }

    internal fun onSettingsChange() {
        // Notify settings change only after calendar is visible, fully initialized and thus
        // BeyondCalendarSettings initialization will not trigger data set change
        if (isShown) {
            calendarPager.adapter?.notifyDataSetChanged()
        }
    }
}
