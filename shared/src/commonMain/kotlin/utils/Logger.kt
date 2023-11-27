package utils

expect class Logger : LoggerInterface {
    companion object {
        fun log(message: String)
    }
}