package com.shamsicalwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.shamsicalwidget.R
import com.shamsicalwidget.util.JalaliCalendar

class HomeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetUpdateService.scheduleDailyAlarm(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetUpdateService.cancelAlarm(context)
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val jalali = JalaliCalendar.today()
            val views = RemoteViews(context.packageName, R.layout.widget_home)

            // Day of week — top
            views.setTextViewText(
                R.id.tv_day_of_week,
                JalaliCalendar.dayName(jalali.dayOfWeek)
            )

            // Day number — large center
            views.setTextViewText(
                R.id.tv_day_number,
                JalaliCalendar.toPersianDigits(jalali.day)
            )

            // Month + Year — bottom
            val monthYear = "${JalaliCalendar.monthName(jalali.month)} ${JalaliCalendar.toPersianDigits(jalali.year)}"
            views.setTextViewText(R.id.tv_month_year, monthYear)

            // Click to open calendar app
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
