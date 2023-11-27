package utils

import android.util.Log

actual class Logger : LoggerInterface {
    actual companion object {
        actual fun log(message: String) {
            Log.d("tremens LOG:", message)
        }
    }
}