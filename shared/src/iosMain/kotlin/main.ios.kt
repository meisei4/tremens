import androidx.compose.ui.window.ComposeUIViewController
import datasources.HabitDBDriverFactory
import datasources.HabitLocalDataSource
import datasources.HabitRepository
import tremens.database.HabitDatabase


//TODO currently only testing on android (via commonMain). fix this module later to work with multiplatform
actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController {
    AppScreen(
        viewModel =  MainViewModel(
            model = MainModel(
                habitRepository = HabitRepository(
                    habitDataSource = HabitLocalDataSource(
                        database = createTestDatabase(
                            driverFactory = HabitDBDriverFactory()
                        )
                    )
                )
            )
        )
    )
}

fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
    val driver = driverFactory.createDriver()
    val database = HabitDatabase(driver)
    //database.habitStorageQueries.insertHabit("anki", Json.encodeToString(List(5) { false }))
    return database
}