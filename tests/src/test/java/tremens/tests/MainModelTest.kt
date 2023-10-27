package tremens.tests

import HabitRowData
import MainModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainModelTest {

    private lateinit var mainModel: MainModel
    private lateinit var habits: MutableState<List<HabitRowData>>

    @Before
    fun setUp() {
        mainModel = MainModel()
        habits = mutableStateOf(emptyList())
    }

    @Test
    fun testGetLastFiveDays() {
        val lastFiveDays = mainModel.getLastFiveDays()
        assertEquals(5, lastFiveDays.size)
    }

    @Test
    fun testAddNewHabit() {
        val newHabit = HabitRowData("Read", List(5) { false })
        mainModel.addNewHabit(habits, newHabit)
        assertEquals(1, habits.value.size)
        assertEquals(newHabit, habits.value.first())
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
