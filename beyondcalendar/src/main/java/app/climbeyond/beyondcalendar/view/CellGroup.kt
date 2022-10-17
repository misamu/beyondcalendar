package app.climbeyond.beyondcalendar.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import app.climbeyond.beyondcalendar.BeyondCalendarSettings
import app.climbeyond.beyondcalendar.childList
import kotlin.math.min

abstract class CellGroup(context: Context, protected val settings: BeyondCalendarSettings) :
        ViewGroup(context) {

    abstract val rowNum: Int
    abstract val colNum: Int

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val specWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val specHeightSize = MeasureSpec.getSize(heightMeasureSpec)
        val specHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        val minSide = when {
            specWidthMode == MeasureSpec.UNSPECIFIED && specHeightMode == MeasureSpec.UNSPECIFIED ->
                throw IllegalStateException("CellLayout can never be left to determine its size")
            specWidthMode == MeasureSpec.UNSPECIFIED -> specHeightSize / rowNum
            specHeightMode == MeasureSpec.UNSPECIFIED -> specWidthSize / colNum
            else -> min(specWidthSize / colNum, specHeightSize / rowNum)
        }
        val minWidth = minSide * colNum
        val minHeight = minSide * rowNum
        val selfMeasuredWidth = when (specWidthMode) {
            MeasureSpec.EXACTLY -> specWidthSize
            MeasureSpec.AT_MOST -> minWidth.coerceAtMost(specWidthSize)
            MeasureSpec.UNSPECIFIED -> minWidth
            else -> specWidthSize
        }
        val selfMeasuredHeight = when (specHeightMode) {
            MeasureSpec.EXACTLY -> specHeightSize
            MeasureSpec.AT_MOST -> minHeight.coerceAtMost(specHeightSize)
            MeasureSpec.UNSPECIFIED -> minHeight
            else -> specHeightSize
        }
        setMeasuredDimension(selfMeasuredWidth, selfMeasuredHeight)

        val childMeasuredWidth = selfMeasuredWidth / colNum
        val childMeasuredHeight = selfMeasuredHeight / rowNum
        val childWidthMeasureSpec =
            MeasureSpec.makeMeasureSpec(childMeasuredWidth, MeasureSpec.AT_MOST)
        val childHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(childMeasuredHeight, MeasureSpec.AT_MOST)
        childList.forEach {
            it.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val offsetTop: Int = (measuredHeight % rowNum) / 2
        val offsetStart: Int = (measuredWidth % colNum) / 2

        val parentTop: Int = paddingTop
        val parentStart: Int = ViewCompat.getPaddingStart(this)

        val isRtl: Boolean = (layoutDirection == View.LAYOUT_DIRECTION_RTL)

        childList.forEachIndexed { i, child ->
            val x: Int = i % colNum
            val y: Int = i / colNum

            val childWidth: Int = child.measuredWidth
            val childHeight: Int = child.measuredHeight

            val childTop: Int = parentTop + offsetTop + (y * childHeight)
            val childStart: Int = parentStart + offsetStart + (x * childWidth)
            val childLeft: Int = if (isRtl) {
                r - childStart - childWidth
            } else {
                childStart
            }

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

}
