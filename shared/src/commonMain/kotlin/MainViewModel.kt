import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class MainViewModel(val model: MainModel) : ViewModel() {
    var habits: MutableState<List<HabitRowData>> = mutableStateOf(emptyList())
    var newHabit: MutableState<HabitRowData> = mutableStateOf(HabitRowData("", List(5) { false }))
    var lastFiveDays: List<String> = model.getLastFiveDays()

    fun addNewHabit(onUpdate: (List<HabitRowData>) -> Unit) {
        model.addNewHabit(habits, newHabit.value)
        onUpdate(habits.value)
    }

    fun updateHabitStatus(targetHabit: HabitRowData, index: Int, updatedStatus: Boolean) {
        model.updateHabitStatus(habits, targetHabit, index, updatedStatus)
    }

    fun setHabits(updatedHabits: List<HabitRowData>) {
        habits.value = updatedHabits
    }

    fun removeHabit(index: Int) {
        model.removeHabit(habits, index)
    }
}
