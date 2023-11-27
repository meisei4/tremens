package mvvm

import ViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import datasources.HabitDataRepository
import datasources.HabitRowData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.Util
import utils.Logger

class MainViewModel(private val habitDataRepo: HabitDataRepository): ViewModel() {

    private val _newHabit: MutableState<String> = mutableStateOf("")
    val newHabit = _newHabit

    var lastFiveDays: List<String> = Util.getLastFiveDaysAsStrings()

    private val _errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())
    val errorMessages = _errorMessages

    private val _habitRows = MutableStateFlow<List<HabitRowData>>(emptyList())
    val habitRows: StateFlow<List<HabitRowData>> = _habitRows.asStateFlow()

    init {
        viewModelScope.launch {
            Logger.log("Starting to collect habit data from the repository")
            try {
                habitDataRepo.selectHabitTrackingJoinedTable().collect { habitList ->
                    _habitRows.value = habitList
                    Logger.log("Habits collected: $habitList")
                }
            } catch (e: Exception) {
                // TODO this is weird that it gets entered sometimes with the message:
                // "DispatchedCoroutine has completed normally"
                Logger.log("Error during habit data collection: ${e.message}")
            }
        }
    }

    fun addHabit() {
        val errors = validateNewHabitInput()
        if (errors.isEmpty() && newHabit.value.isNotEmpty()) {
            viewModelScope.launch {
                //Logger.log("Attempting to add habit: ${newHabit.value}")
                habitDataRepo.addHabit(newHabit.value)
                //Logger.log("New habit added: ${newHabit.value}")
                _newHabit.value = ""
            }
        } else {
            //Logger.log("Failed to add habit due to errors: ${errors.joinToString()}")
            _errorMessages.value = errors
        }
    }

    private fun validateNewHabitInput() : List<String> {
        // Learning Note: This local "errors" val is here to provide ATOMICITY:
        // Because more validation checks will potentially be added to this function in the future,
        // we would like to prevent it from performing any incomplete updates to the error state
        // variable. i.e. we want to return ALL errors in the order that they occur only at the end
        // of the function, rather than adding to the state variable during each validation check.
        // This is because during validation, an error could occur causing the function to be exited
        // but also while having already altered the error state var, making it an incomplete update
        val errors = mutableListOf<String>()

        if (_habitRows.value.any { it.name == newHabit.value }) {
            errors.add("A habit with this name already exists")
            Logger.log("Validation error: A habit with this name already exists") // Log the validation error
        }
        return errors
    }

    fun removeHabit(index: Int) {
        viewModelScope.launch {
            val habitToRemove = habitRows.value[index]
            Logger.log("Removing habit: ${habitToRemove.name}") // Log the habit removal
            habitDataRepo.removeHabit(habitToRemove.name)
        }
    }

    fun updateHabitStatus(currentHabitRow: HabitRowData, dayIndex: Int, newStatus: Boolean) {
        viewModelScope.launch {
            val updatedStatuses = currentHabitRow.lastFiveDatesStatuses.toMutableList().apply {
                this[dayIndex] = newStatus
            }
            val updatedHabitRow = currentHabitRow.copy(lastFiveDatesStatuses = updatedStatuses)
            Logger.log("Updating habit status for: ${currentHabitRow.name}, Day index: $dayIndex, New status: $newStatus") // Log the status update
            habitDataRepo.updateTracking(updatedHabitRow)
        }
    }
}


