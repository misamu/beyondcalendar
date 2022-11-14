BeyondCalendar is an Android calendar library

![calendar](https://user-images.githubusercontent.com/12541406/201667569-b69f3a40-c9bf-4d52-90d6-3a41e1372025.jpg)

## Features

Features include:
* Header showing current year and month
* Show one month in view
* Swipe to change month between defined month interval
* Add markers to any given day

## Download
Aar is available at [builds page](https://github.com/misamu/beyondcalendar/tree/master/builds).

## Example

### layout
```
<app.climbeyond.beyondcalendar.BeyondCalendar
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/black"
    app:headerVisible="true"
    app:headerBgColor="@android:color/darker_gray"
    app:headerTextColor="@android:color/white"
    app:headerTextSize="8sp"
    app:firstDayOfWeek="1"
    app:weekdayTextSize="18sp"
    app:weekdayTextColor="@color/weekday_text_color"
    app:dayTextSize="16sp"
    app:dayTextColor="@color/day_text_color"
    app:dayTextSelectedColor="@color/day_text_selected_color"
    app:dayTextTodayColor="@color/day_text_today_color"
    app:dayTextTodaySelectedColor="@color/day_text_today_selected_color"
    app:selectionColor="@drawable/bc_state_selected" />
```

### Code
```
viewBinding.calendar.onMonthSelected = { date: ZonedDateTime, view: MonthLayout ->
    view.setAccents(
        ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()),
        mutableListOf(DotAccent(ResourcesCompat.getColor(resources, android.R.color.holo_red_light, theme))))
}

viewBinding.calendar.onDateSelected = { date ->
    // Trigger events on date select
}

calendar.onHeaderTodayClicked = {
    // Headers change to today clicked
}
```

## License

    Copyright 2022 Misa Munde

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
