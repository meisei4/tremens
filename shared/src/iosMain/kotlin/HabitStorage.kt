actual abstract class HabitStorage {
    actual suspend fun saveHabit(habit: HabitRowData) {
    }

    actual suspend fun getHabits(): List<HabitRowData> {
        TODO("Not yet implemented")
    }

    actual suspend fun deleteHabit(name: String) {
    }
}