import kotlinx.coroutines.MainScope

actual abstract class ViewModel {
    actual val viewModelScope = MainScope()
    //TODO not sure what to do here yet if its even needed
}