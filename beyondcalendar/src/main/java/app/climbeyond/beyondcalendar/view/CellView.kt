package app.climbeyond.beyondcalendar.view

import android.content.Context
import android.view.View
import app.climbeyond.beyondcalendar.BeyondCalendarSettings

abstract class CellView(context: Context, protected val settings: BeyondCalendarSettings) :
        View(context) {

    var centerX: Float = 0f
    var centerY: Float = 0f
    var cellX:  Float = 0f
    var cellY:  Float = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specHeightSize = MeasureSpec.getSize(heightMeasureSpec)
        val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (specWidthMode == MeasureSpec.UNSPECIFIED || specHeightMode == MeasureSpec.UNSPECIFIED) {
            throw IllegalStateException("CellView can't determine own size")
        }

        setMeasuredDimension(specWidthSize, specHeightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        cellX = w.toFloat()
        cellY = h.toFloat()
        centerX = paddingLeft + (w - paddingLeft - paddingRight) / 2f
        centerY = paddingTop + (h - paddingTop - paddingBottom) / 2f
    }
}
