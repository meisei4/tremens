package utils

expect open class Logger() {
    open fun log(message: String)

    companion object {
        val instance: Logger
    }
}