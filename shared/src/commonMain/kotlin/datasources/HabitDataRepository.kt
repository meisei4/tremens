package datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tremens.database.HabitDatabase
import tremens.database.HabitQueries
import tremens.database.TrackingQueries
import utils.Util

class HabitDataRepository(database: HabitDatabase) {

    private val habitQueries: HabitQueries = database.habitQueries
    private val trackingQueries: TrackingQueries = database.trackingQueries

    suspend fun addHabit(habitRow: HabitRowData) = withContext(Dispatchers.Unconfined) {
        habitQueries.insertHabit(habitRow.name)
        val habitId = habitQueries.getHabitId(habitRow.name).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val datesToStatusMap = lastFiveDatesTimestamps.zip(habitRow.lastFiveDatesStatuses).toMap()
        val datesToInsert = datesToStatusMap.filter { it.value }.keys // only add true statused dates
        datesToInsert.forEach { unixTimestamp -> trackingQueries.insertTracking(habitId, unixTimestamp) }
    }

    suspend fun getAllHabitRows(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val habits = habitQueries.selectAllHabits().executeAsList()

        val habitRowDataList = habits.map { habit ->
            val trackedDatesTimestamps = trackingQueries.selectTrackingForHabitBetweenDates(
                habit.HabitID,
                lastFiveDatesTimestamps[0],
                lastFiveDatesTimestamps[4]
            ).executeAsList()

            // Sneaky one liner that takes care of the true/false state of each lastFiveDates,
            // if the "it" (the unix epoch Long) is found in the returned query then
            // the date gets mapped to true false if otherwise
            val lastFiveDatesStatuses = lastFiveDatesTimestamps.map { it in trackedDatesTimestamps }.toMutableList()
            HabitRowData(habit.Name, lastFiveDatesStatuses)
        }
        return@withContext habitRowDataList
    }

    suspend fun updateTracking(habit: HabitRowData) = withContext(Dispatchers.Unconfined) {
        val habitId = habitQueries.getHabitId(habit.name).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val dateToStatusMap = lastFiveDatesTimestamps.zip(habit.lastFiveDatesStatuses).toMap()

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

    suspend fun removeHabit(habitName: String) = withContext(Dispatchers.Unconfined) {
        //TODO add cascading delete when supported
        trackingQueries.deleteTrackingForHabit(habitName)
        habitQueries.deleteHabit(habitName)
    }

}