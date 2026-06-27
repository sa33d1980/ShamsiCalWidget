package com.shamsicalwidget.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.util.Calendar

class WidgetUpdateService : BroadcastReceiver() {

    override fun onReceive(

        context: Context,

        intent: Intent

    ) {

        val manager =

            AppWidgetManager.getInstance(

                context

            )

        manager.getAppWidgetIds(

            ComponentName(

                context,

                HomeWidgetProvider::class.java

            )

        ).forEach {

            HomeWidgetProvider.updateWidget(

                context,

                manager,

                it

            )

        }

        manager.getAppWidgetIds(

            ComponentName(

                context,

                HomeTextWidgetProvider::class.java

            )

        ).forEach {

            HomeTextWidgetProvider.updateWidget(

                context,

                manager,

                it

            )

        }

        manager.getAppWidgetIds(

            ComponentName(

                context,

                LockScreenWidgetProvider::class.java

            )

        ).forEach {

            LockScreenWidgetProvider.updateWidget(

                context,

                manager,

                it

            )

        }

        scheduleDailyAlarm(

            context

        )

    }

    companion object {

        const val ACTION_UPDATE =

            "com.shamsicalwidget.ACTION_UPDATE_WIDGETS"

        fun scheduleDailyAlarm(

            context: Context

        ) {

            val alarmManager =

                context.getSystemService(

                    Context.ALARM_SERVICE

                ) as AlarmManager

            val intent =

                Intent(

                    context,

                    WidgetUpdateService::class.java

                )

            intent.action = ACTION_UPDATE

            val pending =

                PendingIntent.getBroadcast(

                    context,

                    100,

                    intent,

                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE

                )

            val calendar =

                Calendar.getInstance()

            calendar.add(

                Calendar.DAY_OF_YEAR,

                1

            )

            calendar.set(

                Calendar.HOUR_OF_DAY,

                0

            )

            calendar.set(

                Calendar.MINUTE,

                0

            )

            calendar.set(

                Calendar.SECOND,

                5

            )

            calendar.set(

                Calendar.MILLISECOND,

                0

            )

            alarmManager.setInexactRepeating(

                AlarmManager.RTC,

                calendar.timeInMillis,

                AlarmManager.INTERVAL_DAY,

                pending

            )

        }

    }

}
