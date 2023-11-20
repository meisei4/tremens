package datasources

// This is an interface that abstracts the data source, currently only a local database
// (can be implemented as network database later
interface HabitDataDao {
    suspend fun addHabit(habitRow: HabitRowData)
    suspend fun getAllHabitRows(): List<HabitRowData>

    // TODO figure out why this should or should not be suspended (learn Context and coroutine
    fun updateTracking(habitName: String, updatedTracking: List<Boolean>)
    suspend fun removeHabit(habitName: String)
}