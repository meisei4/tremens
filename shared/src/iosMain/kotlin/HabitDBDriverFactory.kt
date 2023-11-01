import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import tremens.database.HabitDatabase

actual class HabitDBDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(HabitDatabase.Schema, "test.db")
    }
}