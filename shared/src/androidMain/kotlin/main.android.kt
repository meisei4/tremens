import androidx.compose.runtime.Composable

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = AppView(viewModel = MainViewModel(model = MainModel()))