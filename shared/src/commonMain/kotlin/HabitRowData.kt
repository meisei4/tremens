data class HabitRowData(
    val name: String,
    //for now the status is just the last 5 days not-persisted
    //TODO make it some calendar map thing?
    val status: List<Boolean>
) {
    companion object {
        val empty = HabitRowData(
            name = "No Name",
            status = listOf(false, false, false, false, false)
        )

        fun changeAllToTrue(row: HabitRowData): HabitRowData {
            return row.copy(status = listOf(true, true, true, true, true))
        }
    }
}
