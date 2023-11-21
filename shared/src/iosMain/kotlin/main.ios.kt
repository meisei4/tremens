import androidx.compose.ui.window.ComposeUIViewController
import datasources.HabitDBDriverFactory
import datasources.HabitDataRepository
import mvvm.MainViewModel
import tremens.database.HabitDatabase


//TODO currently only testing on android (via commonMain). fix this module later to work with multiplatform
actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController {
    AppScreen(
        viewModel =  MainViewModel(
            habitDataRepo = HabitDataRepository(
                database = createTestDatabase(
                    driverFactory = HabitDBDriverFactory()
                )
            )
        )
    )
}

fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
    val driver = driverFactory.createDriver()
    val database = HabitDatabase(driver)
    return database
}