package utils

// TODO why cant this scope recognize the expect nature of the commonMain utils Logger class
//  this is all very poorly organized and named (including the expect/actual for ios and android
//  fix this and the Dispatchers as well)
class TestLogger : Logger() {
    override fun log(message: String) {
        println("Test LOG:  $message")
    }

     companion object {
         val instance = TestLogger()
    }
}