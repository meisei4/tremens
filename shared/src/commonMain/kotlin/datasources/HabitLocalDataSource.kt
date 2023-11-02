package datasources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tremens.database.HabitDatabase
import tremens.database.HabitStorageQueries

// This is the implementation of the HabitDataSource that interacts with the local database.
class HabitLocalDataSource(database: HabitDatabase): HabitDataSource {

    private val habitQueries: HabitStorageQueries = database.habitStorageQueries

    override suspend fun addHabit(habit: HabitRowData) = withContext(Dispatchers.Unconfined) {
        val statusJson = Json.encodeToString(habit.lastFiveDaysToIsDoneMap)
        habitQueries.insertHabit(habit.name, statusJson)
    }

    override suspend fun getAllHabits(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {
        return@withContext habitQueries.selectHabits().executeAsList().map {
            HabitRowData(it.name, Json.decodeFromString(it.status))
        }
    }

    override fun updateStatus(habitName: String, updatedStatus: List<Boolean>) {
        val statusJson = Json.encodeToString(updatedStatus)
        habitQueries.updateStatus(statusJson, habitName)
    }

    override suspend fun removeHabit(name: String) = withContext(Dispatchers.Unconfined) {
        habitQueries.deleteHabit(name)
    }
}