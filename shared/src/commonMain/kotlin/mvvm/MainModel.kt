import androidx.compose.runtime.MutableState
import datasources.HabitRepository
import datasources.HabitRowData
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt


class MainModel(private val habitRepository: HabitRepository) {

    suspend fun getAllHabits(): List<HabitRowData> {
        return habitRepository.getAllHabits()
    }

    suspend fun addHabit(habit: HabitRowData) {
        //TODO: Should it be assumed that the newHabit is already valid when it enters this function?
        // If someone else looks at this code, how will they know that validation has already occurred?
        // Shouldn't all the Error logic occur in the Model?
        // Is it possible to have a state variable in the Model? Bad practice?
        habitRepository.addHabit(habit)
    }

    suspend fun removeHabit(name: String) {
        habitRepository.removeHabit(name)
    }

    fun getLastFiveDays(): List<String> {
        val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
        return List(5) { i -> current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString() }.reversed()
    }

    fun updateHabitStatus(
        habits: MutableState<List<HabitRowData>>,
        currentHabit: HabitRowData,
        index: Int,
        updatedStatus: Boolean
    ) {
        val updatedHabits = habits.value.map { habit ->
            if (habit.name == currentHabit.name) {
                HabitRowData(habit.name, habit.status.mapIndexed { idx, status ->
                    if (idx == index) updatedStatus else status
                })
            } else habit
        }
        habits.value = updatedHabits
    }

    fun removeHabit(habits: MutableState<List<HabitRowData>>, index: Int) {
        val updatedHabits = habits.value.toMutableList().apply { removeAt(index) }
        habits.value = updatedHabits.toList()
    }
}
