import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

class MainViewModel(val model: MainModel) : ViewModel() {
    var habits: MutableState<List<Pair<String, List<Boolean>>>> = mutableStateOf(emptyList())
    var newHabit: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var lastFiveDays: List<String> = model.getLastFiveDays()

    fun addNewHabit(onUpdate: (List<Pair<String, List<Boolean>>>) -> Unit) {
        model.addNewHabit(habits, newHabit.value.text)
        onUpdate(habits.value)
    }

    fun updateHabitStatus(targetHabit: Pair<String, List<Boolean>>, index: Int, updatedStatus: Boolean) {
        model.updateHabitStatus(habits, targetHabit, index, updatedStatus)
    }

    fun setHabits(updatedHabits: List<Pair<String, List<Boolean>>>) {
        habits.value = updatedHabits
    }

    fun removeHabit(index: Int) {
        model.removeHabit(habits, index)
    }
}
