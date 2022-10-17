package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import app.climbeyond.beyondcalendar.R
import app.climbeyond.beyondcalendar.accent.Accent
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.*


@SuppressLint("ViewConstructor")
class MonthBodyCellDayView(context: Context, settings: BeyondCalendarSettings,
        val date: ZonedDateTime) : CellView(context, settings) {

    private val text: String = date.dayOfMonth.toString()
    private var textPaint: Paint = settings.dayView.defaultTextPaint(date.dayOfWeek)

    private var baseX: Float = 0f
    private var baseY: Float = 0f

    private val accents: MutableList<Accent> = mutableListOf()
    private var accentsCenterX: Float = 0f
    private var accentsCenterY: Float = 0f
    private var accentsSize: Float = 0f
    private var accentsSizeWithMargin: Float = 0f
    private var accentsMargin: Float = 0f
    private var accentPaint: Paint = settings.dayView.cachedAccentPaint

    private var daySelectedPaint: Paint = settings.dayView.daySelectedPaint
    private var daySelected: Boolean = false

    init {
        setBackgroundResource(R.drawable.bc_day_background)
    }

    fun setAccents(accents: Collection<Accent>) {
        this.accents.apply {
            clear()
            addAll(accents.take(3).onEach {
                it.size = accentsSize
            })
        }

        layoutAccents()
    }

    private fun layoutAccents() {
        val centerX: Float = accentsCenterX
        val centerY: Float = accentsCenterY

        when (accents.size) {
            1 -> {
                accents[0].apply {
                    this.offsetX = 0f
                    this.offsetY = 0f
                    this.centerX = centerX
                    this.centerY = centerY
                }
            }
            2 -> {
                accents[0].apply {
                    this.offsetX = -1 * accentsSizeWithMargin
                    this.offsetY = 0f
                    this.centerX = centerX
                    this.centerY = centerY
                }
                accents[1].apply {
                    this.offsetX = accentsSizeWithMargin
                    this.offsetY = 0f
                    this.centerX = centerX
                    this.centerY = centerY
                }
            }
            else -> {
                var startX = -2 * accentsSizeWithMargin
                accents.forEach { accent ->
                    accent.offsetX = startX
                    accent.offsetY = 0f
                    accent.centerX = centerX
                    accent.centerY = centerY
                    startX += accentsSizeWithMargin * 2
                }
            }
        }
    }

    private fun updateMetrics() {
        val fm = textPaint.fontMetrics
        val textWidth = textPaint.measureText(text)
        val textHeight = fm.descent - fm.ascent

        baseX = centerX - textWidth / 2
        baseY = centerY + textHeight / 2 - fm.descent

        accentsCenterX = centerX
        accentsCenterY = baseY + (height - baseY) / 2f
        accentsSize = ((width - paddingLeft - paddingRight) / settings.density) / 7f
        accentsSizeWithMargin = (accentsSize + (0.2 * accentsSize)).toFloat()
        accentsMargin = (0.2 * accentsSize).toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateMetrics()
    }

    internal fun updateState() {
        daySelected = isSelected
        updatePaint()
    }

    private fun updatePaint() {
        val isToday = date.toLocalDate().equals(LocalDate.now(date.zone))

        daySelectedPaint = if (isToday) {
            settings.dayView.daySelectedTodayPaint
        } else {
            settings.dayView.daySelectedPaint
        }

        when {
            isSelected && isToday -> {
                textPaint = settings.dayView.selectedTodayTextPaint
                accentPaint = settings.dayView.selectedTodayAccentPaint
            }
            isSelected -> {
                textPaint = settings.dayView.selectedTextPaint
                accentPaint = settings.dayView.selectedAccentPaint
            }
            isToday -> {
                textPaint = settings.dayView.todayTextPaint
                accentPaint = settings.dayView.todayAccentPaint
            }
            else -> {
                textPaint = settings.dayView.defaultTextPaint(date.dayOfWeek)
                accentPaint = settings.dayView.cachedAccentPaint
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (daySelected) {
            canvas.drawRoundRect(8f, 2f, cellX - 8f, cellY - 2f, 10f, 10f, daySelectedPaint)
        }

        canvas.drawText(text, baseX, baseY, textPaint)

        drawAccents(canvas)
    }

    private fun drawAccents(canvas: Canvas) {
        accents.forEach { accent -> accent.draw(canvas, accentPaint) }
    }

    override fun toString(): String = "DayView($date)"
}
