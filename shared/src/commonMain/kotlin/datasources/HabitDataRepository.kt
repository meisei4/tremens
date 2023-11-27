package datasources

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tremens.database.HabitDatabase
import tremens.database.HabitTrackingQueries
import tremens.database.SelectHabitWithRecentTracking
import utils.Logger
import utils.Util
import utils.ioDispatcher

class HabitDataRepository(database: HabitDatabase) {

    val habitTrackingQueries: HabitTrackingQueries = database.habitTrackingQueries

    suspend fun selectHabitTrackingJoinedTable(): Flow<List<HabitRowData>> = withContext(ioDispatcher()) {
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        //Logger.log("Selecting habit tracking joined table with date range: ${lastFiveDatesTimestamps.first()} to ${lastFiveDatesTimestamps.last()}")

        val habitQueryResultFlow = habitTrackingQueries.selectHabitWithRecentTracking(
            lastFiveDatesTimestamps.first(),
            lastFiveDatesTimestamps.last()
        ).asFlow()
        // TODO If i collect with toList here everything breaks? still unable to effectively debug this or log it
        // val habitQueryResultList = habitQueryResultFlow.toList()
        // log("Habit data selected - Count: ${habitQueryResultList.size}, Data: $habitQueryResultList")

        habitQueryResultFlow.mapToList(this.coroutineContext).map { joinedHabitTrackingQueryResults ->
            joinedHabitTrackingQueryResults.map { joinedHabitTrackingRow ->
                val lastFiveDatesStatuses = getLastFiveDatesStatuses(joinedHabitTrackingRow, lastFiveDatesTimestamps)
                val habitRowData = HabitRowData(joinedHabitTrackingRow.Name, lastFiveDatesStatuses)
                //Logger.log("Mapped HabitRowData for habit ${habitRowData.name}, Statuses: $lastFiveDatesStatuses, Dates: $lastFiveDatesTimestamps")
                habitRowData
            }
        }
    }

    private fun getLastFiveDatesStatuses(joinedHabitTrackingRow: SelectHabitWithRecentTracking, lastFiveDatesTimestamps: List<Long>): List<Boolean> {
        if (joinedHabitTrackingRow.Dates != null) {
            val trackedDatesUnixTimestamps = joinedHabitTrackingRow.Dates.split(",").mapNotNull(String::toLongOrNull)
            return lastFiveDatesTimestamps.map { it in trackedDatesUnixTimestamps }
        } else {
            return lastFiveDatesTimestamps.map { false }
        }
    }

    suspend fun addHabit(habitName: String) = withContext(ioDispatcher()) {
        habitTrackingQueries.insertHabit(habitName)
        // Retrieve the new habit's ID
        val habitId = habitTrackingQueries.getHabitId(habitName).executeAsOne()
        // TODO initial untracked table update (maybe this is needed to recognize need for emission?
        habitTrackingQueries.insertTracking(habitId, -1)
    }

    suspend fun removeHabit(habitName: String) = withContext(ioDispatcher()) {
        //TODO add cascading delete when supported
        habitTrackingQueries.deleteTrackingForHabit(habitName)
        habitTrackingQueries.deleteHabit(habitName)
    }

    suspend fun updateTracking(habit: HabitRowData) = withContext(ioDispatcher()) {
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
}