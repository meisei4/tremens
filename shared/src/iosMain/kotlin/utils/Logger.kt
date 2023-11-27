package utils

actual class Logger : LoggerInterface {
    actual companion object {
        actual fun log(message: String) {
            platform.Foundation.NSLog("DEBUG: $message")
        }
    }
}