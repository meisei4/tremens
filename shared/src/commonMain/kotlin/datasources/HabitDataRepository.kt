package datasources

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tremens.database.HabitDatabase
import tremens.database.HabitQueries
import tremens.database.TrackingQueries
import utils.Util
import kotlin.coroutines.CoroutineContext

// This is the implementation of the HabitDataDao that interacts with the local database.
class HabitDataRepository(database: HabitDatabase): HabitDataDao {

    private val habitQueries: HabitQueries = database.habitQueries
    private val trackingQueries: TrackingQueries = database.trackingQueries

    override suspend fun addHabit(habitRow: HabitRowData) = withContext(Dispatchers.Unconfined) {
        habitQueries.insertHabit(habitRow.name)
        val habitId = habitQueries.getHabitId(habitRow.name).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val datesToStatusMap = lastFiveDatesTimestamps.zip(habitRow.lastFiveDatesStatuses).toMap()
        val datesToInsert = datesToStatusMap.filter { it.value }.keys // only add true statused dates
        datesToInsert.forEach { unixTimestamp -> trackingQueries.insertTracking(habitId, unixTimestamp) }
    }

    override suspend fun getAllHabitRowsFlow(): Flow<List<Flow<HabitRowData>>> = withContext(Dispatchers.IO) {
        habitQueries.selectAllHabits().asFlow().mapToList(this.coroutineContext).map { habits ->
            val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()

            val habitRowDataList = habits.map { habit ->
                trackingQueries.selectTrackingForHabitBetweenDates(
                    habit.HabitID,
                    lastFiveDatesTimestamps[0],
                    lastFiveDatesTimestamps[4]
                ).asFlow().mapToList(coroutineContext).map { timestamps ->
                    val lastFiveDatesStatuses = lastFiveDatesTimestamps.map { it in timestamps }
                    HabitRowData(habit.Name, lastFiveDatesStatuses)
                }

                // Sneaky one liner that takes care of the true/false state of each lastfivedates,
                // if the "it" (the unix epoch Long) is found in the returned query then
                // the date gets mapped to true false if otherwise
            }

            habitRowDataList
        }
    }

        //habitQueries.selectAllHabits().asFlow().mapToList().map { habit ->

    override suspend fun getAllHabitRows(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {

        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        val habits = habitQueries.selectAllHabits().executeAsList()

        val habitRowDataList = habits.map { habit ->
            val trackedDatesTimestamps = trackingQueries.selectTrackingForHabitBetweenDates(
                habit.HabitID,
                lastFiveDatesTimestamps[0],
                lastFiveDatesTimestamps[4]
            ).executeAsList()

            // Sneaky one liner that takes care of the true/false state of each lastfivedates,
            // if the "it" (the unix epoch Long) is found in the returned query then
            // the date gets mapped to true false if otherwise
            val lastFiveDatesStatuses = lastFiveDatesTimestamps.map { it in trackedDatesTimestamps }
            HabitRowData(habit.Name, lastFiveDatesStatuses)
        }

        return@withContext habitRowDataList
    }

    // TODO figure out this suspend issue and Coroutines and context
    override suspend fun updateTracking(habitName: String, updatedTracking: List<Boolean>) {//} = withContext(Dispatchers.Unconfined) {
        val habitId = habitQueries.getHabitId(habitName).executeAsOne()
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()

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

    override suspend fun removeHabit(habitName: String) = withContext(Dispatchers.Unconfined) {
        //TODO add cascading delete when supported
        trackingQueries.deleteTrackingForHabit(habitName)
        habitQueries.deleteHabit(habitName)
    }

}