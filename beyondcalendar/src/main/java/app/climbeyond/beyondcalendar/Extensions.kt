package app.climbeyond.beyondcalendar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat

internal val ViewGroup.childList: List<View>
    get() = (0 until this.childCount).map {
        this.getChildAt(it)
    }

internal fun Context.getColorCompat(resId: Int) = ResourcesCompat.getColor(resources, resId, theme)

internal fun Context.getStyledDimension(attrResId: Int, defaultValue: Float): Float =
    obtainStyledAttributes(intArrayOf(attrResId)).use { it.getDimension(0, defaultValue) }

internal fun Context.getStyledColor(attrResId: Int, defaultColor: Int): Int =
    obtainStyledAttributes(intArrayOf(attrResId)).use { it.getColor(0, defaultColor) }

internal fun Paint.textSize(s: Float): Paint = apply { textSize = s }

internal fun Paint.color(c: Int): Paint = apply { color = c }

internal fun Paint.color(c: ColorStateList, s: BeyondCalendarSettings.AccentSelectedState): Paint = apply {
    color = c.getColorForState(s.value, color)
}

internal fun Paint.typeface(t: Typeface): Paint = apply { typeface = t }

internal fun Paint.isAntiAlias(a: Boolean): Paint = apply { isAntiAlias = a }

internal fun Paint.colorFilter(c: ColorFilter?): Paint = apply { colorFilter = c }

internal fun Paint.style(s: Paint.Style): Paint = apply { style = s }

internal fun Paint.copy(): Paint = Paint(this).apply { typeface = this@copy.typeface }
