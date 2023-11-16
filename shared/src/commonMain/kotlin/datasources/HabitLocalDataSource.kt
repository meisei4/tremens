package datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt
import tremens.database.HabitDatabase
import tremens.database.HabitQueries
import tremens.database.TrackingQueries

val TIME_ZONE = TimeZone.currentSystemDefault()

// This is the implementation of the HabitDataSource that interacts with the local database.
class HabitLocalDataSource(database: HabitDatabase): HabitDataSource {

    private val habitQueries: HabitQueries = database.habitQueries
    private val trackingQueries: TrackingQueries = database.trackingQueries

    override suspend fun addHabit(habit: HabitRowData) = withContext(Dispatchers.Unconfined) {
        habitQueries.insertHabit(habit.name)
        val habitId = habitQueries.getHabitId(habit.name).executeAsOne() //unwraps the Query<> part?
        val lastFiveDates = getLastFiveDates()
        val dateToStatusMap = lastFiveDates.zip(habit.lastFiveDayStatuses).toMap()
        val datesToInsert = dateToStatusMap.filter { it.value == true }.keys //== for explicit clarity that "it" is a map with Boolean values
        datesToInsert.forEach { dateString ->
            val localDate = LocalDate.parse(dateString) // Use LocalDate.parse here
            val instant = localDate.atStartOfDayIn(TIME_ZONE) // Convert LocalDate to Instant at the start of the day
            val unixTimestamp = instant.epochSeconds
            trackingQueries.insertTracking(habitId, unixTimestamp)
        }
    }

    override suspend fun getAllHabits(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {
        val lastFiveDates = getLastFiveDates()
        val lastFiveDatesInstants = lastFiveDates.map {
            LocalDate.parse(it).atStartOfDayIn(TIME_ZONE)
        }
        val lastFiveDatesTimestamps = lastFiveDatesInstants.map { it.epochSeconds }
        val habitNames = habitQueries.selectAllHabitNames().executeAsList()

        // this is kind of crazy but i think its so far the clearest way without doing the antipattern
        val habitRowDataList = habitNames.map { habitName ->
            val trackedDatesTimestamps = trackingQueries.selectTrackingForHabitOnDates(
                habitName,
                lastFiveDatesTimestamps[0],
                lastFiveDatesTimestamps[1],
                lastFiveDatesTimestamps[2],
                lastFiveDatesTimestamps[3],
                lastFiveDatesTimestamps[4]
            ).executeAsList().toSet()

            // Sneaky one liner that takes care of the true/false state of each lastfivedates,
            // if the "it" (the unix epoch Long) is found in the returned query then
            // the date gets mapped to true false if otherwise
            val lastFiveDayStatuses = lastFiveDatesTimestamps.map { it in trackedDatesTimestamps }
            HabitRowData(habitName, lastFiveDayStatuses)
        }

        return@withContext habitRowDataList
    }

    // TODO figure out this suspend issue and Coroutines and context
    override fun updateTracking(habitName: String, updatedTracking: List<Boolean>) {//} = withContext(Dispatchers.Unconfined) {
        val habitId = habitQueries.getHabitId(habitName).executeAsOne()
        val lastFiveDates = getLastFiveDates()
        val lastFiveDatesInstants = lastFiveDates.map { LocalDate.parse(it).atStartOfDayIn(TIME_ZONE) } // Parse to LocalDate and get Instant
        val lastFiveDatesTimestamps = lastFiveDatesInstants.map { it.epochSeconds }

        val dateToStatusMap = lastFiveDatesTimestamps.zip(updatedTracking).toMap()

        dateToStatusMap.forEach { (date, status) ->
            if (status) {
                // If status is true, insert or ignore (if it already exists: check the Tracking insert query)
                trackingQueries.insertTracking(habitId, date)
            } else {
                // If status is false, delete (if it exists)
                trackingQueries.deleteTrackingForDate(habitId, date)
            }
        }
    }

    override suspend fun removeHabit(name: String) = withContext(Dispatchers.Unconfined) {
        trackingQueries.deleteTrackingForHabit(name)
        habitQueries.deleteHabit(name)
    }

    // TODO figure out where to put these auxiliary/util methods
    private fun getLastFiveDates(): List<String> {
        val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
        return List(5) { i ->
            current.minus(i, DateTimeUnit.DAY).toString() //should return yyyy-MM-dd
        }.reversed()
    }

}