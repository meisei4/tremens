package datasources

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import tremens.database.HabitDatabase

actual class HabitDBDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(HabitDatabase.Schema, context, "test.db")
    }
}