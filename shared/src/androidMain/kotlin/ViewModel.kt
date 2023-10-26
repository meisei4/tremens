import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as androidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope

actual abstract class ViewModel actual constructor() : androidXViewModel() {
    actual val viewModelScope: CoroutineScope = androidXViewModelScope
}