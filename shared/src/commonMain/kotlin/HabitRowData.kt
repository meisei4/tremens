data class HabitRowData(
    val name: String,
    //for now the status is just the last 5 days not-persisted
    //TODO make it some calendar map thing?
    val status: List<Boolean>
)
