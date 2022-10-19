package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

@SuppressLint("ViewConstructor")
class MonthLegendCellView(context: Context, settings: BeyondCalendarSettings,
        private var weekday: DayOfWeek) :
        CellView(context, settings) {

    private val text: String = weekday.getDisplayName(TextStyle.SHORT, settings.locale)
    private val textPaint: Paint = settings.weekdayView.cachedTextPaint(weekday)
    private var baseX: Float = 0f
    private var baseY: Float = 0f

    private fun updateMetrics() {
        val fm = textPaint.fontMetrics
        val textWidth = textPaint.measureText(text)
        val textHeight = fm.descent - fm.ascent
        baseX = centerX - textWidth / 2
        baseY = centerY + textHeight / 2 - fm.descent
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMetrics()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText(text, baseX, baseY, textPaint)
    }

    override fun toString(): String = "WeekDayView($weekday)"
}
