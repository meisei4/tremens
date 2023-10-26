import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.CoroutineScope

expect abstract class ViewModel() {
    val viewModelScope: CoroutineScope
}