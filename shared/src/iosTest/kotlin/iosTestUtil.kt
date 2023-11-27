import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.inMemoryDriver
import tremens.database.HabitDatabase

actual fun testDbConnection(): SqlDriver {
    return inMemoryDriver(HabitDatabase.Schema)
}