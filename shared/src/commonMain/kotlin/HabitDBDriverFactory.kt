import app.cash.sqldelight.db.SqlDriver

expect class HabitDBDriverFactory {
    fun createDriver(): SqlDriver
}
