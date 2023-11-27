import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import tremens.database.HabitDatabase

actual fun testDbConnection(): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    .also { HabitDatabase.Schema.create(it) }