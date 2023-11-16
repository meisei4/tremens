package tremens.tests

import datasources.HabitDBDriverFactory
import datasources.HabitLocalDataSource
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
class HabitLocalDataSourceTest {

    private lateinit var habitDatabase: HabitDatabase
    private lateinit var dataSource: HabitLocalDataSource

    @Before
    fun setup() {
        val context = RuntimeEnvironment.getApplication()
        assertNotNull("Context should not be null", context)
        habitDatabase = createTestDatabase(HabitDBDriverFactory(context))
        dataSource = HabitLocalDataSource(habitDatabase)
    }

    //TODO this method is copy pasted from ios and android, figure out how to introduce a
    // good util thing in commonMain maybe?
    fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
        val driver = driverFactory.createDriver()
        return HabitDatabase(driver)
    }

    @Test
    fun addHabit_adds_habit_to_db_checks_existence_removes_habit_from_db() = runTest {
        val habit = HabitRowData("Test Habit", listOf(false, false, false, false, false))

        dataSource.addHabit(habit)
        val allHabits = dataSource.getAllHabits()
        assertTrue(allHabits.contains(habit))

        dataSource.removeHabit(habit.name)
        val updatedHabits = dataSource.getAllHabits()
        assertFalse(updatedHabits.contains(habit))
    }
}

