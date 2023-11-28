package utils

actual open class Logger {
    actual open fun log(message: String) {
            platform.Foundation.NSLog("tremens LOG: $message")
        }
    actual companion object {
        actual val instance = Logger()
    }
}