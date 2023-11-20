package mvvm

import MainModel
import ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import datasources.HabitRowData
import kotlinx.coroutines.launch
import utils.Util

class MainViewModel(val model: MainModel) : ViewModel() {

    var newHabit: MutableState<HabitRowData> = mutableStateOf(HabitRowData("", List(5) { false }))
    var lastFiveDays: List<String> = Util.getLastFiveDaysAsStrings()
    var errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())
    val habitRows: MutableState<List<HabitRowData>> = mutableStateOf(listOf())

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            habitRows.value = model.getAllHabits()
        }
    }
    fun addHabit() {
        validateNewHabitInput()
        if (errorMessages.value.isEmpty()) {
            viewModelScope.launch {
                model.addHabit(newHabit.value)
                loadHabits()
            }
        }
    }

    private fun validateNewHabitInput() {
        // Learning Note: This local "errors" val is here to provide ATOMICITY:
        // Because more validation checks will potentially be added to this function in the future,
        // we would like to prevent it from performing any incomplete updates to the error state
        // variable. i.e. we want to return ALL errors in the order that they occur only at the end
        // of the function, rather than adding to the state variable during each validation check.
        // This is because during validation, an error could occur causing the function to be exited
        // but also while having already altered the error state var, making it an incomplete update
        val errors = mutableStateListOf<String>()

        // This is no longer enterable based on the enabled attribute of the AddHabit Button, but
        // keeping as an example for future validation
        if (newHabit.value.name.isBlank()) {
            errors.add("Habit name cannot be empty")
        }

        errorMessages.value = errors
    }

    fun updateHabitStatus(targetHabit: HabitRowData, dayColumnIndex: Int, updatedStatus: Boolean) {
        model.updateHabitStatus(habitRows, targetHabit, dayColumnIndex, updatedStatus)
    }

    fun removeHabit(index: Int) {
        viewModelScope.launch {
            val habitToRemove = habitRows.value[index]
            model.removeHabit(habitToRemove.name)
            loadHabits()
        }
    }
}
