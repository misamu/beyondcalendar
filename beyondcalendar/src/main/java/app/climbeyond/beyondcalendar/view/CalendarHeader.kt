package app.climbeyond.beyondcalendar.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import app.climbeyond.beyondcalendar.BeyondCalendar
import app.climbeyond.beyondcalendar.R
import java.time.format.TextStyle
import java.util.*


@SuppressLint("ViewConstructor")
class CalendarHeader(val beyondCalendar: BeyondCalendar, context: Context) : LinearLayout(context) {

    private val padding = resources.getDimension(R.dimen.header_padding).toInt()
    private val headerText: TextView

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setPadding(padding * 2, padding, padding * 2, padding)

        headerText = TextView(context).apply {
            textSize = beyondCalendar.settings.calendarHeader.textSize
            layoutParams = TableLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
            setTextColor(beyondCalendar.settings.calendarHeader.textColor)
        }

        updateHeaderText()
        updateLayout()
    }

    fun updateHeaderText() {
        val date = beyondCalendar.calendarPager.getCurrentPageDate()
        headerText.text = context.getString(R.string.header_text, date.year,
                date.month.getDisplayName(TextStyle.FULL, beyondCalendar.settings.locale))
    }

    private fun updateLayout() {
        // remove all children
        removeAllViews()

        setBackgroundColor(beyondCalendar.settings.calendarHeader.bgColor)

        addView(headerText)

        val image = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_today, context.theme)
        addView(ImageView(context).apply {
            //setPadding(0, padding, 0, 0)
            setImageDrawable(image)
            setOnClickListener {
                beyondCalendar.onHeaderTodayClicked()
            }
        })
    }
}
