import kotlinx.coroutines.CoroutineScope
import androidx.lifecycle.ViewModel as androidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope

actual abstract class ViewModel actual constructor() : androidXViewModel() {
    actual val viewModelScope: CoroutineScope = androidXViewModelScope
    //TODO: not sure what to do here yet if its even needed
}