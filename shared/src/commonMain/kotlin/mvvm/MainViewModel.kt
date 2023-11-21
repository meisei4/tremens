package mvvm

import MainModel
import ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import datasources.HabitRowData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.Util

class MainViewModel(val model: MainModel) : ViewModel() {

    var newHabit: MutableState<HabitRowData> = mutableStateOf(HabitRowData("", List(5) { false }))
    var lastFiveDays: List<String> = Util.getLastFiveDaysAsStrings()
    var errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())
    private val _habitRowsStateFlow = MutableStateFlow<List<HabitRowData>>(emptyList())
    val habitRows: StateFlow<List<HabitRowData>> = _habitRowsStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            model.habitRows.collect { habits ->
                _habitRowsStateFlow.value = habits
            }
        }
    }

    fun addHabit() {
        validateNewHabitInput()
        if (errorMessages.value.isEmpty()) {
            viewModelScope.launch {
                model.addHabit(newHabit.value)
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
        // launch creates a new thread to allow for suspend functions to not block the main thread
        // Coroutine is just a set of comannds on a separate thread
        viewModelScope.launch {
            model.updateHabitStatus(_habitRowsStateFlow, targetHabit, dayColumnIndex, updatedStatus)
        }
    }

    fun removeHabit(index: Int) {
        viewModelScope.launch {
            val habitToRemove = _habitRowsStateFlow.value[index]
            model.removeHabit(habitToRemove.name)
        }
    }
}
