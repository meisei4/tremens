package datasources

data class HabitRowData(
    val name: String,
    //for now the status is just the last 5 days not-persisted
    //TODO make it some calendar map thing?
    val lastFiveDayStatuses: List<Boolean>
) {

    //LEARNING NOTE: companion objects
    companion object {
        val empty = HabitRowData(
            name = "No Name",
            lastFiveDayStatuses = listOf(false, false, false, false, false)
        )

        fun changeAllToTrue(row: HabitRowData): HabitRowData {
            return row.copy(lastFiveDayStatuses = listOf(true, true, true, true, true))
        }
    }
}
