import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import datasources.HabitDBDriverFactory
import datasources.HabitLocalDataSource
import mvvm.MainViewModel
import tremens.database.HabitDatabase

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = AppScreen(
    viewModel = MainViewModel(
        model = MainModel(
            habitDataSource = HabitLocalDataSource(
                database = createTestDatabase(HabitDBDriverFactory(LocalContext.current))
            )
        )
    )
)

fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
    val driver = driverFactory.createDriver()
    val database = HabitDatabase(driver)
    return database
}