package datasources

// This is an interface that abstracts the data source, currently only a local database
// (can be implemented as network database later
interface HabitDataSource {
    suspend fun addHabit(habit: HabitRowData)
    suspend fun getAllHabits(): List<HabitRowData>

    // TODO figure out why this should or should not be suspended
    fun updateStatus(habitName: String, updatedStatus: List<Boolean>)
    suspend fun removeHabit(name: String)
}