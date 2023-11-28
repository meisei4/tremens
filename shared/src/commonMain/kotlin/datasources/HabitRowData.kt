package datasources

data class HabitRowData(
    val name: String,
    val lastFiveDatesStatuses: List<Boolean>
)