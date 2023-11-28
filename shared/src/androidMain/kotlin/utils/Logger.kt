package utils

import android.util.Log

actual open class Logger {
    actual open fun log(message: String) {
        Log.d("tremens LOG: ", message)
    }

    actual companion object {
        actual val instance = Logger()
    }
}