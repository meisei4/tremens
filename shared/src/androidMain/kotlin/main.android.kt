import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import datasources.HabitDBDriverFactory
import datasources.HabitDataRepository
import tremens.database.HabitDatabase
import mvvm.MainViewModel
import utils.Logger

actual fun getPlatformName(): String = "Android"

@Composable
fun MainView() = AppScreen(
    viewModel = MainViewModel(
        habitDataRepo = HabitDataRepository(
            database = createTestDatabase(HabitDBDriverFactory(LocalContext.current)),
            logger = Logger.instance
        )
    )
)

fun createTestDatabase(driverFactory: HabitDBDriverFactory): HabitDatabase {
    val driver = driverFactory.createDriver()
    val database = HabitDatabase(driver)
    return database
}