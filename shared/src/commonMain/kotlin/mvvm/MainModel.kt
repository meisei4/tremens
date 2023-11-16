import androidx.compose.runtime.MutableState
import datasources.HabitDataSource
import datasources.HabitRowData
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayAt


class MainModel(private val habitDataSource: HabitDataSource) {

    suspend fun getAllHabits(): List<HabitRowData> {
        return habitDataSource.getAllHabits()
    }

    suspend fun addHabit(habit: HabitRowData) {
        // TODO: Should it be assumed that the newHabit is already valid when it enters this function?
        // If someone else looks at this code, how will they know that validation has already occurred?
        // Shouldn't all the Error logic occur in the Model?
        // Is it possible to have a state variable in the Model? Bad practice?
        habitDataSource.addHabit(habit)
    }

    suspend fun removeHabit(name: String) {
        habitDataSource.removeHabit(name)
    }

    fun updateHabitStatus(
        habits: MutableState<List<HabitRowData>>,
        currentHabit: HabitRowData,
        dayColumnIndex: Int,
        updatedTrackingValue: Boolean
    ) {
        val updatedHabits = habits.value.map { habit ->
            if (habit.name == currentHabit.name) {
                val updatedTracking = habit.lastFiveDayStatuses.mapIndexed { i, isDoneStatus ->
                    if (i == dayColumnIndex) updatedTrackingValue else isDoneStatus
                }
                habitDataSource.updateTracking(habit.name, updatedTracking) // Persist the updated status to the database
                HabitRowData(habit.name, updatedTracking)
            } else habit
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

    // TODO figure out where to put these auxiliary/util methods

    fun getLastFiveDays(): List<String> {
        val current = Clock.System.todayAt(TimeZone.currentSystemDefault())
        return List(5) { i -> current.minus(i, DateTimeUnit.DAY).dayOfMonth.toString() }.reversed()
    }
}

