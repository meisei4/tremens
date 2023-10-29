package tremens.tests

import HabitRowData
import MainModel
import MainViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

const val EMPTY_STRING = ""
class MainModelTest {

    private lateinit var mainModel: MainModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var habits: MutableState<List<HabitRowData>>

    @Before
    fun setUp() {
        mainModel = MainModel()
        mainViewModel = MainViewModel(mainModel)
        habits = mutableStateOf(emptyList())
    }

    @Test
    fun testGetLastFiveDays() {
        val lastFiveDays = mainModel.getLastFiveDays()
        assertEquals(5, lastFiveDays.size)
    }

    @Test
    fun testAddNewHabit_InputNOTEMPTY_HabitListUPDATED_ErrorMessagesEMPTY() {
        val newHabit = HabitRowData("Read", List(5) { false })
        mainModel.addNewHabit(habits, newHabit)

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
        mainViewModel.addNewHabit { }

        assertEquals(0, habits.value.size)
        assertTrue(mainViewModel.errorMessages.value.contains("Habit name cannot be empty"))
    }

    @Test
    fun testUpdateHabitStatus() {
        val initialHabit = HabitRowData("Exercise", List(5) { false })
        habits.value = listOf(initialHabit)
        mainModel.updateHabitStatus(habits, initialHabit, 0, true)

        val updatedStatus = habits.value.first().status.first()
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
