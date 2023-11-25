package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt

class Util {

    companion object {
        private val TIME_ZONE: TimeZone = TimeZone.currentSystemDefault()

        fun getLastFiveDaysAsStrings(): List<String> {
            val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
            return List(5) { i -> current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString() }.reversed()
        }

        fun getLastFiveDatesAsTimestamps(): List<Long> {
            val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
            return List(5) { i -> current.minus(i, DateTimeUnit.DAY).atStartOfDayIn(TIME_ZONE).epochSeconds}.reversed()
        }
    }



}