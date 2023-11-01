import androidx.compose.runtime.Composable
import android.content.Context
import tremens.database.HabitDatabase

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = AppView(viewModel = MainViewModel(model = MainModel(habitStorage = MainHabitStorage(
    database = HabitDatabase(HabitDBDriverFactory().createDriver())
))))

fun HabitDBDriverFactory(): HabitDBDriverFactory {

}
