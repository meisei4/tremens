package tremens.tests

import datasources.HabitRowData
import MainModel
import mvvm.MainViewModel
import androidx.compose.runtime.MutableState
import app.cash.sqldelight.Query
import datasources.HabitDataRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import tremens.database.HabitDatabase
import tremens.database.HabitQueries
import tremens.database.TrackingQueries
import utils.Util

const val EMPTY_STRING = ""

//TODO This model test does not work anymore after database was refactored.
// all tests are via emulator.

@ExperimentalCoroutinesApi
class MainModelTest {

    private lateinit var mainModel: MainModel
    private lateinit var habits: MutableState<List<HabitRowData>>
    private lateinit var mainViewModel: MainViewModel
    private lateinit var habitDatabase: HabitDatabase
    private lateinit var habitQueries: HabitQueries
    private lateinit var trackingQueries: TrackingQueries
    private lateinit var habitDataRepository: HabitDataRepository

    @Before
    fun setUp() {
        // Mocks
        habitQueries = mockk(relaxed = true)
        trackingQueries = mockk(relaxed = true)
        habitDatabase = mockk {
            every { habitQueries } returns this@MainModelTest.habitQueries
            every { trackingQueries } returns this@MainModelTest.trackingQueries
        }
        habitDataRepository = HabitDataRepository(habitDatabase)

        // MainModel and MainViewModel setup
        mainModel = MainModel(habitDataRepository)
        mainViewModel = MainViewModel(mainModel)
    }

    @Test
    fun `addHabit inserts habit into database`() = runBlockingTest {
        // Arrange
        val habit = HabitRowData("Drink Water", listOf(true, false, true, true, false))
        val mockQuery = mockk<Query<Long>> {
            every { executeAsOne() } returns 1L
        }
        coEvery { habitQueries.insertHabit(any()) } just Runs
        coEvery { habitQueries.getHabitId(any()) } returns mockQuery

        // Act
        habitDataRepository.addHabit(habit)

        // Assert
        coVerify(exactly = 1) { habitQueries.insertHabit("Drink Water") }
        coVerify(exactly = 1) { mockQuery.executeAsOne() }
    }

    @Test
    fun testGetLastFiveDays() {
        val lastFiveDays = Util.getLastFiveDaysAsStrings()
        assertEquals(5, lastFiveDays.size)
    }

    @Test
    fun testAddNewHabit_InputNOTEMPTY_HabitListUPDATED_ErrorMessagesEMPTY() {
        val newHabit = HabitRowData("Read", List(5) { false })
        //mainModel.addHabit(habits, newHabit)

        assertEquals(1, habits.value.size)
        assertEquals(newHabit, habits.value.first())
        assertEquals(0, mainViewModel.errorMessages.value.size)
    }

    @Test
    fun testAddNewHabit_InputEMPTY_HabitListEMPTY_ErrorMessagesUPDATED() {
        mainViewModel.errorMessages.value = emptyList()

        mainViewModel.newHabit.value = HabitRowData(EMPTY_STRING, List(5) { false })
        // No need to pass in any function to the addNewHabit because we are only
        // testing the call to the private validateNewHabitInput function (have not
        // decided to make validateNewHabitInput public yet.)
        //mainViewModel.addHabit { }

        assertEquals(0, habits.value.size)
        assertTrue(mainViewModel.errorMessages.value.contains("Habit name cannot be empty"))
    }

    @Test
    fun testUpdateHabitStatus() {
        val initialHabit = HabitRowData("Exercise", List(5) { false })
        habits.value = listOf(initialHabit)
        mainModel.updateHabitStatus(habits, initialHabit, 0, true)

        val updatedStatus = habits.value.first().lastFiveDatesStatuses.first()
        assertTrue(updatedStatus)
    }

    @Test
    fun testRemoveHabit() {
        val initialHabit = HabitRowData("Exercise", List(5) { false })
        habits.value = listOf(initialHabit)
        mainModel.removeHabit(habits, 0)

        assertTrue(habits.value.isEmpty())
    }
}
