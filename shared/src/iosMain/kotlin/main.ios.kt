import androidx.compose.ui.window.ComposeUIViewController


//TODO currently only testing on android (via commonMain). fix this module later to work with multiplatform
actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { MainViewModel(MainModel()) }