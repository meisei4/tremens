interface HabitStorage {
    suspend fun saveHabit(habit: HabitRowData)
    suspend fun getHabits(): List<HabitRowData>
    suspend fun deleteHabit(name: String)
    // ... other CRUD operations
}