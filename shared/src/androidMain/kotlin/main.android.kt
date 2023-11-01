import androidx.compose.runtime.Composable
import android.content.Context

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = AppView(viewModel = MainViewModel(model = MainModel(habitStorage = MainHabitStorage(
    database = HabitDBDriverFactory(context = Context).createDriver()))))