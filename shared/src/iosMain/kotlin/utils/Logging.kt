package utils

actual fun log(message: String) {
    platform.Foundation.NSLog("DEBUG: $message")
}