package com.shamsicalwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.shamsicalwidget.R
import com.shamsicalwidget.util.JalaliCalendar

/**
 * Lock Screen Widget — identical layout to Home widget but uses
 * the lock_widget_info.xml provider definition which sets
 * widgetCategory to keyguard|home_screen.
 */
class LockScreenWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val jalali = JalaliCalendar.today()
            // Lock screen uses same layout as home widget
            val views = RemoteViews(context.packageName, R.layout.widget_home)

            views.setTextViewText(
                R.id.tv_day_of_week,
                JalaliCalendar.dayName(jalali.dayOfWeek)
            )
            views.setTextViewText(
                R.id.tv_day_number,
                JalaliCalendar.toPersianDigits(jalali.day)
            )
            val monthYear = "${JalaliCalendar.monthName(jalali.month)} ${JalaliCalendar.toPersianDigits(jalali.year)}"
            views.setTextViewText(R.id.tv_month_year, monthYear)

            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
