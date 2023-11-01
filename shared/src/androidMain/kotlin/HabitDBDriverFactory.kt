import android.content.Context
import tremens.database.HabitDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class HabitDBDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(HabitDatabase.Schema, context, "test.db")
    }
}