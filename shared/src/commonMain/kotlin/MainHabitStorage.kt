import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tremens.database.HabitDatabase
import tremens.database.HabitStorageQueries

open class MainHabitStorage(database: HabitDatabase) : HabitStorage {

    private val habitQueries: HabitStorageQueries = database.habitStorageQueries

    fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
        val driver = driverFactory.createDriver()
        val database = HabitDatabase(driver)
        habitQueries.insertHabit("anki", Json.encodeToString(List(5) { false }))
        return database
    }

    override suspend fun saveHabit(habit: HabitRowData) = withContext(Dispatchers.Unconfined) {
        val statusJson = Json.encodeToString(habit.status)
        habitQueries.insertHabit(habit.name, statusJson)
    }


    override suspend fun getHabits(): List<HabitRowData> = withContext(Dispatchers.Unconfined) {
        return@withContext habitQueries.selectHabits().executeAsList().map {
            HabitRowData(it.name, Json.decodeFromString(it.status))
        }
    }

    override suspend fun deleteHabit(name: String) = withContext(Dispatchers.Unconfined) {
        habitQueries.deleteHabit(name)
    }
}
