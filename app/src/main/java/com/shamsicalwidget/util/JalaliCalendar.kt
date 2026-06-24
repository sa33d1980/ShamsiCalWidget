package com.shamsicalwidget.util

import java.util.Calendar

/**
 * Converts Gregorian date to Jalali (Shamsi) calendar.
 * Pure Kotlin — no external dependencies.
 */
data class JalaliDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val dayOfWeek: Int  // Calendar.SUNDAY=1 .. Calendar.SATURDAY=7
)

object JalaliCalendar {

    val monthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    // Index matches Calendar.DAY_OF_WEEK constants (1=Sunday .. 7=Saturday)
    private val dayNames = arrayOf(
        "",           // 0 unused
        "یکشنبه",    // 1 Sunday
        "دوشنبه",    // 2 Monday
        "سه‌شنبه",   // 3 Tuesday
        "چهارشنبه",  // 4 Wednesday
        "پنجشنبه",   // 5 Thursday
        "جمعه",      // 6 Friday
        "شنبه"       // 7 Saturday
    )

    fun today(): JalaliDate {
        val cal = Calendar.getInstance()
        return fromGregorian(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.DAY_OF_WEEK)
        )
    }

    fun monthName(month: Int): String = monthNames[month - 1]

    fun dayName(dayOfWeek: Int): String = dayNames[dayOfWeek]

    private fun fromGregorian(gy: Int, gm: Int, gd: Int, dow: Int): JalaliDate {
        val g2d = gregorianToDN(gy, gm, gd)
        val j2d = g2d - gregorianToDN(622, 3, 22)
        val (jy, jm, jd) = dnToJalali(j2d)
        return JalaliDate(jy, jm, jd, dow)
    }

    private fun gregorianToDN(y: Int, m: Int, d: Int): Int {
        val c = y / 100
        val yr = y - 100 * c
        return (146097 * c) / 4 + (1461 * yr) / 4 + (153 * m - 457) / 5 + d - 306
    }

    private fun dnToJalali(jdn: Int): Triple<Int, Int, Int> {
        var j = jdn - jalaliToDN(475, 1, 1)
        val cycle = Math.floorDiv(j, 1029983)
        val cyear = j - 1029983 * cycle
        val ycycle: Int = if (cyear == 1029982) {
            2820
        } else {
            val a1 = cyear / 366
            val a2 = cyear % 366
            (2134 * a1 + 2816 * a2 + 2815) / 1028522 + a1 + 1
        }
        var jy = ycycle + 2820 * cycle + 474
        if (jy <= 0) jy--
        val jyd = jdn - jalaliToDN(jy, 1, 1) + 1
        val jm: Int
        val jd: Int
        if (jyd <= 186) {
            jm = ((jyd - 1) / 31) + 1
            jd = jyd - 31 * (jm - 1)
        } else {
            jm = ((jyd - 7) / 30) + 1
            jd = jyd - (30 * (jm - 1) + 6)
        }
        return Triple(jy, jm, jd)
    }

    private fun jalaliToDN(jy: Int, jm: Int, jd: Int): Int {
        val epbase = jy - if (jy >= 0) 474 else 473
        val epyear = 474 + Math.floorMod(epbase, 2820)
        return jd +
                (if (jm <= 7) (jm - 1) * 31 else (jm - 1) * 30 + 6) +
                (epyear * 682 - 110) / 2816 +
                (epyear - 1) * 365 +
                Math.floorDiv(epbase, 2820) * 1029983 +
                1948319
    }

    /** Convert integer to Persian Eastern Arabic digits */
    fun toPersianDigits(number: Int): String = number.toString().map {
        when (it) {
            '0' -> '۰'; '1' -> '۱'; '2' -> '۲'; '3' -> '۳'; '4' -> '۴'
            '5' -> '۵'; '6' -> '۶'; '7' -> '۷'; '8' -> '۸'; '9' -> '۹'
            else -> it
        }
    }.joinToString("")
}
