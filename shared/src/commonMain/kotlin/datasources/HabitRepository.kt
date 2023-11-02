package datasources

class HabitRepository(private val habitDataSource: HabitDataSource) {

    suspend fun addHabit(habit: HabitRowData) {
        habitDataSource.addHabit(habit)
    }

    suspend fun getAllHabits(): List<HabitRowData> {
        return habitDataSource.getAllHabits()
    }

    fun updateTracking(habitName: String, updatedTracking: List<Boolean>) {
        habitDataSource.updateTracking(habitName, updatedTracking)
    }

    suspend fun removeHabit(name: String) {
        habitDataSource.removeHabit(name)
    }
}