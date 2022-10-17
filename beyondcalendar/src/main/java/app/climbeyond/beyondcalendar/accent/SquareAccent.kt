package app.climbeyond.beyondcalendar.accent

import android.graphics.Canvas
import android.graphics.Paint

class SquareAccent(override var color: Int? = null, key: Any = Any()) : Accent(key) {

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        val oldColor = paint.color
        paint.color = color ?: paint.color
        canvas.drawRect(x - size, y - size, x + size, y + size, paint)
        paint.color = oldColor
    }
}
