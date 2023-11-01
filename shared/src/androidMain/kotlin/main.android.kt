import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import tremens.database.HabitDatabase

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = AppScreen(viewModel = MainViewModel(model = MainModel(habitStorage = MainHabitStorage(
    database = HabitDatabase(HabitDBDriverFactory(LocalContext.current).createDriver())
))))