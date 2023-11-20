package tremens.tests

import datasources.HabitDBDriverFactory
import datasources.HabitDataRepository
import datasources.HabitRowData
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.RuntimeEnvironment
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import tremens.database.HabitDatabase


//TODO this test class is not yet working due to dependency/gradle issues
@RunWith(RobolectricTestRunner::class)
class HabitDataRepositoryTest {

    private lateinit var habitDatabase: HabitDatabase
    private lateinit var dataRepository: HabitDataRepository

    @Before
    fun setup() {
        val context = RuntimeEnvironment.getApplication()
        assertNotNull("Context should not be null", context)
        habitDatabase = createTestDatabase(HabitDBDriverFactory(context))
        dataRepository = HabitDataRepository(habitDatabase)
    }

    //TODO this method is copy pasted from ios and android, figure out how to introduce a
    // good util thing in commonMain maybe?
    fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
        val driver = driverFactory.createDriver()
        return HabitDatabase(driver)
    }

    @Test
    fun addHabit_adds_habit_to_db_checks_existence_removes_habit_from_db() = runTest {
        val habitRow = HabitRowData("Test Habit", listOf(false, false, false, false, false))

        dataRepository.addHabit(habitRow)
        val allHabits = dataRepository.getAllHabitRows()
        assertTrue(allHabits.contains(habitRow))

        dataRepository.removeHabit(habitRow.name)
        val updatedHabits = dataRepository.getAllHabitRows()
        assertFalse(updatedHabits.contains(habitRow))
    }
}

