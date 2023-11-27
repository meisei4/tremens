import datasources.HabitDataRepository
import datasources.HabitRowData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import tremens.database.HabitDatabase
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
        //TODO all the logging code in the
        val driver = testDbConnection()
        // Add SQL statements to drop tables if they exist
        driver.execute(null, "DROP TABLE IF EXISTS Habit", 0)
        driver.execute(null, "DROP TABLE IF EXISTS Tracking", 0)
        HabitDatabase.Schema.create(driver)
        database = HabitDatabase(driver)
        repository = HabitDataRepository(database)
    }

    @AfterTest
    fun tearDown() {
        // TODO figure out db closing in sqldelight KMM if needed
    }

    @Test
    fun testAddHabit() = runTest {
        val habitName = "Read Books"
        repository.addHabit(habitName)

        val habitId = repository.habitTrackingQueries.getHabitId(habitName).executeAsOneOrNull()
        assertNotNull(habitId, "Habit ID should not be null for added habit")

        val results = repository.selectHabitTrackingJoinedTable().first()
        assertTrue(results.any { it.name == habitName }, "Newly added habit should be present")
    }

    @Test
    fun testRemoveHabit() = runTest {
        val habitName = "Exercise"
        repository.addHabit(habitName)
        repository.removeHabit(habitName)

        val habitId = repository.habitTrackingQueries.getHabitId(habitName).executeAsOneOrNull()
        assertEquals(null, habitId, "Habit ID should be null after habit is removed")

        val results = repository.selectHabitTrackingJoinedTable().first()
        assertTrue(results.none { it.name == habitName }, "Removed habit should not be present")
    }

    @Test
    fun testSelectHabitTrackingJoinedTable() = runTest {
        val habitName = "Drink Water"
        repository.addHabit(habitName)

        val results = repository.selectHabitTrackingJoinedTable().first()
        assertTrue(results.any { it.name == habitName }, "Habit should be in the tracking table")
    }

    @Test
    fun testUpdateTracking() = runTest {
        val habitName = "Meditation"
        repository.addHabit(habitName)

        val updatedStatuses = listOf(true, false, true, false, true)
        val habitRowData = HabitRowData(habitName, updatedStatuses)
        repository.updateTracking(habitRowData)

        val results = repository.selectHabitTrackingJoinedTable().first()
        val habitData = results.find { it.name == habitName }

        assertNotNull(habitData, "Updated habit should be present")
        assertEquals(habitData.lastFiveDatesStatuses, updatedStatuses, "Habit statuses should be updated")
    }
}