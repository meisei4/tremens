import datasources.HabitDataRepository
import datasources.HabitRowData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tremens.database.HabitDatabase
import utils.TestLogger
import utils.Util
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HabitDataRepositoryTest {

    private lateinit var database: HabitDatabase
    private lateinit var repository: HabitDataRepository

    @BeforeTest
    fun setup() = runTest {
        val driver = testDbConnection()
        driver.execute(null, "DROP TABLE IF EXISTS Habit", 0)
        driver.execute(null, "DROP TABLE IF EXISTS Tracking", 0)
        HabitDatabase.Schema.create(driver)
        database = HabitDatabase(driver)
        repository = HabitDataRepository(database, logger = TestLogger.instance)
    }

    @AfterTest
    fun tearDown() {
        // TODO figure out db closing in sqldelight KMM if needed
    }

    // MOST IMPORTANT test for the Flow emission and collection functionality
    @Test
    fun selectHabitTrackingJoinedTable_WhenHabitAdded_ShouldReturnValidJoinedDataFlow() = runTest {
        val habitName = "Read Books"
        repository.addHabit(habitName)

        val habitId = repository.habitTrackingQueries.getHabitId(habitName).executeAsOneOrNull()
        assertNotNull(habitId, "Habit ID should not be null for added habit")

        val result = repository.selectHabitTrackingJoinedTable().first()
        assertNotNull(result.find { it.name == habitName }, "Habit should be found after being added")
    }

    @Test
    fun removeHabit_ShouldRemoveTrackingEntriesForHabit() = runTest {
        val habitName = "Read"
        repository.addHabit(habitName)
        val habitId = repository.habitTrackingQueries.getHabitId(habitName).executeAsOne()

        repository.removeHabit(habitName)

        val trackingEntries = repository.habitTrackingQueries.getTrackingForHabit(habitId).executeAsList()
        assertTrue(trackingEntries.isEmpty(), "Tracking entries should be removed when habit is deleted")
    }

    @Test
    fun updateTracking_ShouldReflectCorrectStatusInDatabase() = runTest {
        val habitName = "Meditate"
        repository.addHabit(habitName)
        val habitId = repository.habitTrackingQueries.getHabitId(habitName).executeAsOne()

        val lastFiveDates = Util.getLastFiveDatesAsTimestamps()
        val statuses = listOf(true, false, true, false, true) // Example statuses for the last five dates
        val habitRowData = HabitRowData(habitName, statuses)

        repository.updateTracking(habitRowData)

        for (index in lastFiveDates.indices) {
            val date = lastFiveDates[index]
            val status = statuses[index]
            val isTracked = repository.habitTrackingQueries.isDateTrackedForHabit(habitId, date).executeAsOne()
            assertEquals(status, isTracked, "Database tracking status should match the updated status for date: $date")
        }
    }
}