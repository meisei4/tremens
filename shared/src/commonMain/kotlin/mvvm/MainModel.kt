import androidx.compose.runtime.MutableState
import datasources.HabitDataDao
import datasources.HabitRowData


class MainModel(private val habitDataDao: HabitDataDao) {

    suspend fun getAllHabits(): List<HabitRowData> {
        return habitDataDao.getAllHabitRows()
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

    fun updateHabitStatus(
        habits: MutableState<List<HabitRowData>>,
        currentHabitRow: HabitRowData,
        dayColumnIndex: Int,
        updatedTrackingValue: Boolean
    ) {
        val updatedHabits = habits.value.map { habitRow ->
            if (habitRow.name == currentHabitRow.name) {
                val updatedTracking = habitRow.lastFiveDatesStatuses.mapIndexed { i, isDoneStatus ->
                    if (i == dayColumnIndex) updatedTrackingValue else isDoneStatus
                }
                habitDataDao.updateTracking(habitRow.name, updatedTracking) // Persist the updated status to the database
                HabitRowData(habitRow.name, updatedTracking)
            } else habitRow
        }
        // TODO is it ok to do this here? or should the Database and mutable state variable
        // be connected more atomically? IS THE MUTABLE STATE VARIABLE EVEN NEEDED ANYMORE?
        // need to study concept of Flow?

        habits.value = updatedHabits
    }

    fun removeHabit(habits: MutableState<List<HabitRowData>>, index: Int) {
        val updatedHabits = habits.value.toMutableList().apply { removeAt(index) }
        habits.value = updatedHabits.toList()
    }

}

