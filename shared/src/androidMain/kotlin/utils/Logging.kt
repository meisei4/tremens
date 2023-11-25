package utils

import android.util.Log

actual fun log(message: String) {
    Log.d("tremens LOG:", message)
}