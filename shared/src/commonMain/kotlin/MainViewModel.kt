import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class MainViewModel(val model: MainModel) : ViewModel() {
    var habits: MutableState<List<HabitRowData>> = mutableStateOf(emptyList())
    var newHabit: MutableState<HabitRowData> = mutableStateOf(HabitRowData("", List(5) { false }))
    var lastFiveDays: List<String> = model.getLastFiveDays()
    var errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())

    fun addNewHabit(onUpdate: (List<HabitRowData>) -> Unit) {
        validateNewHabitInput()
        model.addNewHabit(habits, newHabit.value)
        onUpdate(habits.value)
    }

    private fun validateNewHabitInput() {
        // Learning Note: This local "errors" val is here to provide ATOMICITY:
        // Because more validation checks will potentially be added to this function in the future,
        // we would like to prevent it from performing any incomplete updates to the error state
        // variable. i.e. we want to return ALL errors in the order that they occur only at the end
        // of the function, rather than adding to the state variable during each validation check.
        // This is because during validation, an error could occur causing the function to be exited
        //  but also while having already altered the error state var, making it an incomplete update
        val errors = mutableListOf<String>()

        if (newHabit.value.name.isBlank()) {
            errors.add("Habit name cannot be empty")
        }

        // at somepoint add a validation to check whether or not the newHabit exists in the db yet

        errorMessages.value = errors
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
