package datasources

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import tremens.database.HabitDatabase
import tremens.database.HabitTrackingQueries
import tremens.database.SelectHabitWithRecentTracking
import utils.Logger
import utils.Util
import utils.ioDispatcher
import kotlin.coroutines.coroutineContext

class HabitDataRepository(database: HabitDatabase, private val logger: Logger) {

    val habitTrackingQueries: HabitTrackingQueries = database.habitTrackingQueries

    suspend fun selectHabitTrackingJoinedTable(): Flow<List<HabitRowData>> {
        logger.log("Starting to select habit tracking joined table")
        val lastFiveDatesTimestamps = Util.getLastFiveDatesAsTimestamps()
        logger.log("Date range for selection: ${lastFiveDatesTimestamps.first()} to ${lastFiveDatesTimestamps.last()}")

        logger.log("Executing SQLDelight query to get habit tracking joined table.")
        val habitQueryResultFlow = habitTrackingQueries.selectHabitWithRecentTracking(
            lastFiveDatesTimestamps.first(),
            lastFiveDatesTimestamps.last()
        ).asFlow()

        logger.log("Converted query result to Flow.")

        return habitQueryResultFlow
            .mapToList(context = coroutineContext) // The coroutine context is the one from the coroutine that calls this method
            .map { joinedHabitTrackingQueryResults ->
                logger.log("Mapping results to HabitRowData objects, number of results: ${joinedHabitTrackingQueryResults.size}")
                joinedHabitTrackingQueryResults.map { joinedHabitTrackingRow ->
                    logger.log("Mapping row for habit: ${joinedHabitTrackingRow.Name}")
                    val lastFiveDatesStatuses = getLastFiveDatesStatuses(joinedHabitTrackingRow, lastFiveDatesTimestamps)
                    HabitRowData(joinedHabitTrackingRow.Name, lastFiveDatesStatuses)
                }
            }
            .catch { e ->
                logger.log("Error collecting flow: ${e.message}")
                throw e
            }
    }

    suspend fun addHabit(habitName: String) = withContext(ioDispatcher()) {
        logger.log("Inserting habit with name: $habitName into the database...")
        habitTrackingQueries.insertHabit(habitName)
        logger.log("Completed adding habit: $habitName")
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
                habitTrackingQueries.insertTracking(habitId, date)
            } else {
                habitTrackingQueries.deleteTrackingForDate(habitId, date)
            }
        }

    }

    //AUXILIARY
    private fun getLastFiveDatesStatuses(joinedHabitTrackingRow: SelectHabitWithRecentTracking, lastFiveDatesTimestamps: List<Long>): List<Boolean> {
        return if (joinedHabitTrackingRow.Dates != null) {
            val trackedDatesUnixTimestamps = joinedHabitTrackingRow.Dates.split(",").mapNotNull(String::toLongOrNull)
            lastFiveDatesTimestamps.map { it in trackedDatesUnixTimestamps }
        } else {
            lastFiveDatesTimestamps.map { false }
        }
    }

}