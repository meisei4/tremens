import androidx.compose.runtime.MutableState
import datasources.HabitDataDao
import datasources.HabitRowData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MainModel(private val habitDataDao: HabitDataDao) {

    // suspend

    val habitRows: Flow<List<HabitRowData>> = habitDataDao.getAllHabitRowsFlow()

    // suspend functions can block the main thread when called (interrupt)
    // requires to be ran in a thread separate than mainthread
    suspend fun getAllHabits(): Flow<List<HabitRowData>> {
        return habitDataDao.getAllHabitRowsFlow() //blocking call because it needs to access database
    // (app cant continue until database sends response back.)
    }

    suspend fun addHabit(habitRow: HabitRowData) {
        // TODO: Should it be assumed that the newHabit is already valid when it enters this function?
        // If someone else looks at this code, how will they know that validation has already occurred?
        // Shouldn't all the Error logic occur in the Model?
        // Is it possible to have a state variable in the Model? Bad practice?
        habitDataDao.addHabit(habitRow)
    }

    suspend fun removeHabit(habitName: String) {
        habitDataDao.removeHabit(habitName)
    }

    suspend fun updateHabitStatus(
        habits: MutableStateFlow<List<HabitRowData>>,
        currentHabitRow: HabitRowData,
        dayColumnIndex: Int,
        updatedTrackingValue: Boolean
    ) {
        val updatedHabits = habits.value.map { habitRow ->
            if (habitRow.name == currentHabitRow.name) {
                val updatedTracking = habitRow.lastFiveDatesStatuses.mapIndexed { i, isDoneStatus ->
                    if (i == dayColumnIndex) updatedTrackingValue else isDoneStatus
                }
                habitDataDao.updateTracking(
                    habitRow.name,
                    updatedTracking
                ) // Persist the updated status to the database
                HabitRowData(habitRow.name, updatedTracking)
            } else habitRow
        }
        habits.value = updatedHabits
    }

    fun removeHabit(habits: MutableState<List<HabitRowData>>, index: Int) {
        val updatedHabits = habits.value.toMutableList().apply { removeAt(index) }
        habits.value = updatedHabits.toList()
    }

}

