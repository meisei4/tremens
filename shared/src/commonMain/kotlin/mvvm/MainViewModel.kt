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

    private val logger = Logger.instance

    private val _newHabit: MutableState<String> = mutableStateOf("")
    val newHabit = _newHabit

    var lastFiveDays: List<String> = Util.getLastFiveDaysAsStrings()

    private val _errorMessages: MutableState<List<String>> = mutableStateOf(emptyList())
    val errorMessages = _errorMessages

    private val _habitRows = MutableStateFlow<List<HabitRowData>>(emptyList())
    val habitRows: StateFlow<List<HabitRowData>> = _habitRows.asStateFlow()

    init {
        viewModelScope.launch {
            logger.log("Starting to collect habit data from the repository")
            try {
                habitDataRepo.selectHabitTrackingJoinedTable().collect { habitList ->
                    _habitRows.value = habitList
                    logger.log("Habits collected: $habitList")
                }
            } catch (e: Exception) {
                logger.log("Error during habit data collection: ${e.message}")
            }
        }
    }

    fun addHabit() {
        val errors = validateNewHabitInput()
        if (errors.isEmpty() && newHabit.value.isNotEmpty()) {
            viewModelScope.launch {
                logger.log("Attempting to add habit: ${newHabit.value}")
                habitDataRepo.addHabit(newHabit.value)
                logger.log("New habit added: ${newHabit.value}")
                _newHabit.value = ""
            }
        } else {
            logger.log("Failed to add habit due to errors: ${errors.joinToString()}")
            _errorMessages.value = errors
        }
    }

    private fun validateNewHabitInput() : List<String> {
        val errors = mutableListOf<String>()

        if (_habitRows.value.any { it.name == newHabit.value }) {
            errors.add("A habit with this name already exists")
            logger.log("Validation error: A habit with this name already exists")
        }
        return errors
    }

    fun removeHabit(index: Int) {
        viewModelScope.launch {
            val habitToRemove = habitRows.value[index]
            logger.log("Removing habit: ${habitToRemove.name}")
            habitDataRepo.removeHabit(habitToRemove.name)
        }
    }

    fun updateHabitStatus(currentHabitRow: HabitRowData, dayIndex: Int, newStatus: Boolean) {
        viewModelScope.launch {
            val updatedStatuses = currentHabitRow.lastFiveDatesStatuses.toMutableList().apply {
                this[dayIndex] = newStatus
            }
            val updatedHabitRow = currentHabitRow.copy(lastFiveDatesStatuses = updatedStatuses)
            logger.log("Updating habit status for: ${currentHabitRow.name}, Day index: $dayIndex, New status: $newStatus")
            habitDataRepo.updateTracking(updatedHabitRow)
        }
    }
}