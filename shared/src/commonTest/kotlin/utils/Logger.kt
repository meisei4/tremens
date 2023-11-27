package utils

//TODO this might be a better way to avoid mocking but unsure how to allow it to recognize the commonMain expected class
class Logger {
    companion object {
        fun log(message: String) {
            println("Test LOG:  $message")
        }
    }
}