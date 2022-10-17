package app.climbeyond.beyondcalendar.accent

import android.graphics.Canvas
import android.graphics.Paint

class DotAccent(override var color: Int? = null, key: Any = Any()) : Accent(key) {

    override fun draw(canvas: Canvas, x: Float, y: Float, paint: Paint) {
        val oldColor = paint.color
        paint.color = color ?: paint.color
        canvas.drawCircle(x, y, size, paint)
        paint.color = oldColor
    }
}
