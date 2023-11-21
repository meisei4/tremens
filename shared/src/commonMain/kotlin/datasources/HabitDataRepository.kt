package datasources

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tremens.database.HabitDatabase
import tremens.database.HabitTrackingQueries
import utils.Util

class HabitDataRepository(database: HabitDatabase) {

    private val habitTrackingQueries: HabitTrackingQueries = database.habitTrackingQueries

    suspend fun addHabit(habitRow: HabitRowData) = withContext(Dispatchers.Unconfined) {
        habitTrackingQueries.insertHabit(habitRow.name)
        val habitId = habitTrackingQueries.getHabitId(habitRow.name).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val datesToStatusMap = lastFiveDatesTimestamps.zip(habitRow.lastFiveDatesStatuses).toMap()
        val datesToInsert = datesToStatusMap.filter { it.value }.keys // only add true statused dates
        datesToInsert.forEach { unixTimestamp -> habitTrackingQueries.insertTracking(habitId, unixTimestamp) }
    }

    suspend fun getAllHabitRows(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val habits = habitTrackingQueries.selectAllHabits().executeAsList()

        val habitRowDataList = habits.map { habit ->
            val trackedDatesTimestamps = habitTrackingQueries.selectTrackingForHabitBetweenDates(
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

    suspend fun selectHabitTrackingJoinedTable(habitName: String): Flow<List<HabitRowData>> = withContext(Dispatchers.Unconfined)  {
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        return@withContext habitTrackingQueries.selectHabitWithRecentTracking(
            lastFiveDatesTimestamps[0],
            lastFiveDatesTimestamps[4],
            habitName // TODO select all habits? just one?
        ).asFlow() // Convert Query to Flow
            .mapToList(this.coroutineContext) // Execute the Query and convert the results to a List
            .map { resultList ->
                resultList.map { selectHabitWithRecentTracking ->
                    val splitDates = if (selectHabitWithRecentTracking.Dates?.isNotBlank() == true) {
                        selectHabitWithRecentTracking.Dates.split(",")
                    } else {
                        emptyList()
                    }
                    val dateLongs = splitDates.mapNotNull { it.toLongOrNull() }
                    val lastFiveDatesStatuses = lastFiveDatesTimestamps.map { timestamp ->
                        timestamp in dateLongs
                    }
                    HabitRowData(selectHabitWithRecentTracking.Name, lastFiveDatesStatuses)
                }
            }
    }



    suspend fun updateTracking(habit: HabitRowData) = withContext(Dispatchers.Unconfined) {
        val habitId = habitTrackingQueries.getHabitId(habit.name).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val dateToStatusMap = lastFiveDatesTimestamps.zip(habit.lastFiveDatesStatuses).toMap()

        dateToStatusMap.forEach { (date, status) ->
            if (status) {
                // If status is true, insert or ignore (if it already exists: check the Tracking insert query)
                habitTrackingQueries.insertTracking(habitId, date)
            } else {
                // If status is false, delete (if it exists)
                habitTrackingQueries.deleteTrackingForDate(habitId, date)
            }
        }
    }



    suspend fun removeHabit(habitName: String) = withContext(Dispatchers.Unconfined) {
        //TODO add cascading delete when supported
        habitTrackingQueries.deleteTrackingForHabit(habitName)
        habitTrackingQueries.deleteHabit(habitName)
    }

}