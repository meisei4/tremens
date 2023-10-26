import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

class MainViewModel(private val model: MainModel) : ViewModel() {
    var habitStatus: MutableList<Pair<String, List<Boolean>>> = mutableListOf()
    var newHabit: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
    var lastFiveDays: List<String> = model.getLastFiveDays()

    fun addNewHabit(onUpdate: (List<Pair<String, List<Boolean>>>) -> Unit) {
        onUpdate(model.addNewHabit(habitStatus, newHabit.value.text))
    }

    fun updateHabitStatus(
        currentHabit: Pair<String, List<Boolean>>,
        index: Int,
        updatedStatus: Boolean,
    ) {
        habitStatus = model.updateHabitStatus(habitStatus, currentHabit, index, updatedStatus).toMutableList()
    }

    fun removeHabit(index: Int) {
        habitStatus = model.removeHabit(habitStatus, index).toMutableList()
    }
}