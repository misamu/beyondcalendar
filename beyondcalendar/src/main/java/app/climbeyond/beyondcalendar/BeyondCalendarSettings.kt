package app.climbeyond.beyondcalendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import java.time.DayOfWeek
import java.time.ZoneId
import java.util.*

class BeyondCalendarSettings(private val context: Context) {

    val density = context.resources.displayMetrics.density
    val weekdayView = WeekdayView()
    val dayView = DayView()

    var timeZone: TimeZone = TimeZone.getDefault()
    val timeZoneId: ZoneId
        get() = timeZone.toZoneId()
    var locale: Locale = Locale.getDefault()
    var firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY
    val dayOfWeekOffset: Int
        get() = DayOfWeek.values().indexOf(firstDayOfWeek)

    inner class WeekdayView {

        var textColor: Int = -1
            set(value) {
                field = value
                cachedTextPaints.values.forEach { it.color = value }
            }

        var textSize: Float = context.getStyledDimension(android.R.attr.textSize,
                context.resources.getDimension(R.dimen.week_day_text_size))
            set(value) {
                field = value
                cachedTextPaints.values.forEach { it.textSize = value }
            }

        private val textFilterColorMap: MutableMap<DayOfWeek, Int> = mutableMapOf()

        private var basePaint: Paint = Paint().textSize(this@WeekdayView.textSize)
            .color(this@WeekdayView.textColor).typeface(Typeface.DEFAULT).isAntiAlias(true)
            set(value) {
                field = value
                cachedTextPaints = defaultTextPaints()
            }

        internal fun cachedTextPaint(weekday: DayOfWeek): Paint = cachedTextPaints[weekday]
            ?: throw IllegalStateException("CalendarSettings.cachedTextPaints: $weekday not found")

        private var cachedTextPaints: Map<DayOfWeek, Paint> = defaultTextPaints()

        private fun defaultTextPaints() = DayOfWeek.values().associateWith {
            basePaint.copy().colorFilter(textFilterColorMap[it]?.let { color ->
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            })
        }

        internal fun setTextFilterColor(weekday: DayOfWeek, color: Int) {
            textFilterColorMap[weekday] = color
            cachedTextPaints[weekday]?.colorFilter(textFilterColorMap[weekday]?.let {
                PorterDuffColorFilter(it, PorterDuff.Mode.SRC_ATOP)
            } ?: throw IllegalStateException("CalendarSettings.setTextFilterColor: $weekday not found"))
        }
    }

    inner class DayView {

        var textColor: Int = context.getColorCompat(R.color.day_text_color)
            set(value) {
                field = value
                cachedTextPaints.values.forEach { it.color(value) }
            }

        var textColorSelected: Int = context.getColorCompat(R.color.day_text_selected_color)
            set(value) {
                field = value
                selectedTextPaint.color = value
            }

        var textColorToday: Int = context.getColorCompat(R.color.day_text_today_color)
            set(value) {
                field = value
                todayTextPaint.color = value
            }

        var textColorTodaySelected: Int =
            context.getColorCompat(R.color.day_text_today_selected_color)
            set(value) {
                field = value
                selectedTodayTextPaint.color = value
            }

        var textSize: Float = context.getStyledDimension(android.R.attr.textSize,
                context.resources.getDimension(R.dimen.day_text_size))
            set(value) {
                field = value
                cachedTextPaints.values.forEach { it.textSize(value) }
                selectedTextPaint.textSize(value)
                selectedTodayTextPaint.textSize(value)
                todayTextPaint.textSize(value)
            }

        private val textFilterColorMap: MutableMap<DayOfWeek, Int> = mutableMapOf()

        private var baseCirclePaint: Paint = Paint().isAntiAlias(true).style(Paint.Style.FILL)
            set(value) {
                field = value

                daySelectedPaint = daySelectedPaint()
                daySelectedTodayPaint = daySelectedTodayPaint()
            }

        private var baseTextPaint: Paint = Paint().textSize(this@DayView.textSize)
            .color(this@DayView.textColor).typeface(Typeface.DEFAULT).isAntiAlias(true)
            set(value) {
                field = value

                cachedTextPaints = cachedTextPaints()
                selectedTextPaint = initializeSelectedTextPaint()
                selectedTodayTextPaint = selectedTodayTextPaint()
                todayTextPaint = todayTextPaint()
            }

        private var baseAccentPaint: Paint = Paint().isAntiAlias(true).style(Paint.Style.FILL)
            set(value) {
                field = value

                cachedAccentPaint = defaultAccentPaint()
                selectedAccentPaint = selectedAccentPaint()
                selectedTodayAccentPaint = todaySelectedAccentPaint()
                todayAccentPaint = todayAccentPaint()
            }

        internal var daySelectedPaint = daySelectedPaint()
        internal var daySelectedTodayPaint = daySelectedTodayPaint()

        private fun daySelectedPaint() = baseCirclePaint.copy()
            .color(context.getColorCompat(R.color.day_circle_color))

        private fun daySelectedTodayPaint() = baseCirclePaint.copy()
            .color(context.getStyledColor(android.R.attr.colorPrimary,
                    context.getColorCompat(R.color.day_today_circle_color)))

        internal fun defaultTextPaint(weekday: DayOfWeek): Paint = cachedTextPaints[weekday]
            ?: throw IllegalStateException(
                    "CalendarSettings.defaultTextPaint: defaultTextPaints $weekday not found")

        private var cachedTextPaints: Map<DayOfWeek, Paint> = cachedTextPaints()
        internal var selectedTextPaint: Paint = initializeSelectedTextPaint()
        internal var selectedTodayTextPaint: Paint = selectedTodayTextPaint()
        internal var todayTextPaint: Paint = todayTextPaint()

        private fun cachedTextPaints() = DayOfWeek.values().associateWith {
            baseTextPaint.copy().colorFilter(textFilterColorMap[it]?.let { color ->
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            })
        }

        private fun todayTextPaint() = baseTextPaint.copy().color(textColorToday)

        private fun initializeSelectedTextPaint() = baseTextPaint.copy()
            .typeface(Typeface.DEFAULT_BOLD).color(textColorSelected)

        private fun selectedTodayTextPaint() = baseTextPaint.copy()
            .typeface(Typeface.DEFAULT_BOLD).color(textColorTodaySelected)


        internal var cachedAccentPaint = defaultAccentPaint()
        internal var selectedAccentPaint = selectedAccentPaint()
        internal var selectedTodayAccentPaint = todaySelectedAccentPaint()
        internal var todayAccentPaint = todayAccentPaint()

        private fun defaultAccentPaint() = baseAccentPaint.copy()
            .color(context.getStyledColor(android.R.attr.colorAccent,
                    context.getColorCompat(R.color.day_dot_color)))

        private fun todayAccentPaint() = baseAccentPaint.copy()
            .color(context.getStyledColor(android.R.attr.colorAccent,
                    context.getColorCompat(R.color.day_today_dot_color)))

        private fun selectedAccentPaint() = baseAccentPaint.copy()
            .color(context.getStyledColor(android.R.attr.colorAccent,
                    context.getColorCompat(R.color.day_selected_dot_color)))

        private fun todaySelectedAccentPaint() = baseAccentPaint.copy()
            .color(context.getStyledColor(android.R.attr.colorAccent,
                    context.getColorCompat(R.color.day_selected_today_dot_color)))


        internal fun setCircleColorStateList(colorStateList: ColorStateList) {
            daySelectedPaint.color(colorStateList, AccentSelectedState.SELECTED)
            daySelectedTodayPaint.color(colorStateList, AccentSelectedState.SELECTED_TODAY)
        }

        internal fun setTextFilterColor(weekday: DayOfWeek, color: Int) {
            textFilterColorMap[weekday] = color
            cachedTextPaints[weekday]?.colorFilter(textFilterColorMap[weekday]?.let {
                PorterDuffColorFilter(it, PorterDuff.Mode.SRC_ATOP)
            } ?: throw IllegalStateException("CalendarSettings.setTextFilterColor: $weekday not found"))
        }

        internal fun setAccentColorStateList(colorStateList: ColorStateList) {
            cachedAccentPaint.color(colorStateList, AccentSelectedState.DEFAULT)
            selectedAccentPaint.color(colorStateList, AccentSelectedState.SELECTED)
            selectedTodayAccentPaint.color(colorStateList, AccentSelectedState.SELECTED_TODAY)
            todayAccentPaint.color(colorStateList, AccentSelectedState.TODAY)
        }
    }

    /**
     * Selected stated for accent's
     */
    enum class AccentSelectedState(val value: IntArray) {

        DEFAULT(intArrayOf()),
        SELECTED(intArrayOf(android.R.attr.state_selected)),
        TODAY(intArrayOf(android.R.attr.state_active)),
        SELECTED_TODAY(intArrayOf(android.R.attr.state_selected, android.R.attr.state_active))
    }
}
