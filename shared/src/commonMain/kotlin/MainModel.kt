import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt

class MainModel {
    fun getLastFiveDays(): List<String> {
        val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
        return List(5) { i ->
            current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString()
        }.reversed()
    }

    fun addNewHabit(habitStatus: MutableList<Pair<String, List<Boolean>>>, newHabit: String): List<Pair<String, List<Boolean>>> {
        return habitStatus + (newHabit to List(5) { false })
    }

    fun updateHabitStatus(
        habitStatus: MutableList<Pair<String, List<Boolean>>>,
        currentHabit: Pair<String, List<Boolean>>,
        index: Int,
        updatedStatus: Boolean,
    ): List<Pair<String, List<Boolean>>> {
        return habitStatus.map { habit ->
            if (habit.first == currentHabit.first) {
                habit.first to habit.second.mapIndexed { idx, status ->
                    if (idx == index) updatedStatus else status
                }
            } else habit
        }
    }

    fun removeHabit(habitStatus: MutableList<Pair<String, List<Boolean>>>, index: Int): List<Pair<String, List<Boolean>>> {
        return habitStatus.toMutableList().apply { removeAt(index) }
    }
}
