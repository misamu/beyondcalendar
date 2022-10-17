package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import app.climbeyond.beyondcalendar.BeyondCalendarSettings

@SuppressLint("ViewConstructor")
class MonthBodyCellEmptyView(context: Context, settings: BeyondCalendarSettings) :
        CellView(context, settings) {

    override fun onDraw(canvas: Canvas?) {
        // not in use
    }

    override fun toString(): String = "EmptyView"
}
