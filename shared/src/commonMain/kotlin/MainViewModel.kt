import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue

class MainViewModel : ViewModel() {
    var habitStatus: MutableList<Pair<String, List<Boolean>>> = mutableListOf()
    var newHabit: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))

    fun addNewHabit(
        onUpdate: (List<Pair<String, List<Boolean>>>) -> Unit
    ) {
        onUpdate(habitStatus + (newHabit.value.text to List(5) { false }))
    }
}