package app.climbeyond.beyondcalendar.accent

import android.graphics.Canvas
import android.graphics.Paint

abstract class Accent(val key: Any = Any()) {

    internal var size: Float = 0f
    abstract val color: Int?

    var centerX: Float = 0f
    var centerY: Float = 0f
    var offsetX: Float = 0f
    var offsetY: Float = 0f

    abstract fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint)

    open fun draw(canvas: Canvas, paint: Paint) {
        canvas.save()
        draw(canvas, centerX + offsetX, centerY + offsetY, paint)
        canvas.restore()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is Accent) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
