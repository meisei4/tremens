import androidx.compose.runtime.MutableState
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt

class MainModel {
    fun getLastFiveDays(): List<String> {
        val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
        return List(5) { i -> current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString() }.reversed()
    }

    fun addNewHabit(habitStatus: MutableState<List<Pair<String, List<Boolean>>>>, newHabit: String) {
        val updatedHabits = habitStatus.value + (newHabit to List(5) { false })
        habitStatus.value = updatedHabits
    }

    fun updateHabitStatus(
        habits: MutableState<List<Pair<String, List<Boolean>>>>,
        currentHabit: Pair<String, List<Boolean>>,
        index: Int,
        updatedStatus: Boolean
    ) {
        val updatedHabits = habits.value.map { habit ->
            if (habit.first == currentHabit.first) {
                habit.first to habit.second.mapIndexed { idx, status ->
                    if (idx == index) updatedStatus else status
                }
            } else habit
        }
        habits.value = updatedHabits
    }

    fun removeHabit(habits: MutableState<List<Pair<String, List<Boolean>>>>, index: Int) {
        val updatedHabits = habits.value.toMutableList().apply { removeAt(index) }
        habits.value = updatedHabits.toList()
    }
}
