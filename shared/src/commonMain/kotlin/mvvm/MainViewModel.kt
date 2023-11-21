package mvvm

import ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import datasources.HabitDataRepository
import datasources.HabitRowData
import kotlinx.coroutines.launch
import utils.Util


class MainViewModel(private val habitDataRepo: HabitDataRepository): ViewModel() {

    var newHabit: MutableState<HabitRowData> = mutableStateOf(HabitRowData("", MutableList(5) { false }))
    var lastFiveDays: List<String> = Util.getLastFiveDaysAsStrings()
    var errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())
    val habitRows: MutableState<List<HabitRowData>> = mutableStateOf(listOf())

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            habitRows.value = habitDataRepo.getAllHabitRows()
        }
    }

    fun addHabit() {
        validateNewHabitInput()
        if (errorMessages.value.isEmpty()) {
            viewModelScope.launch {
                habitDataRepo.addHabit(newHabit.value)
            }
        }
        loadHabits() // TODO this constant reloading of the state variable "habitRows" will be fixed with Flows
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

    fun removeHabit(index: Int) {
        viewModelScope.launch {
            val habitToRemove = habitRows.value[index]
            habitDataRepo.removeHabit(habitToRemove.name)
        }
        loadHabits() // TODO this constant reloading of the state variable "habitRows" will be fixed with Flows
    }

    fun updateHabitStatus(currentHabitRow: HabitRowData, dayIndex: Int, newStatus: Boolean) {
        viewModelScope.launch {
            habitRows.value = habitRows.value.map { habitRow ->
                if (habitRow.name == currentHabitRow.name) {
                    val updatedStatuses = habitRow.lastFiveDatesStatuses.toMutableList().apply {
                        this[dayIndex] = newStatus
                    }
                    val updatedHabitRow = habitRow.copy(lastFiveDatesStatuses = updatedStatuses)
                    habitDataRepo.updateTracking(updatedHabitRow)

                    updatedHabitRow
                } else {
                    habitRow
                }
            }
        }
    }
}

