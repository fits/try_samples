import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

fun main() {
    val channel = Channel<String>()

    GlobalScope.launch {
        (1..10).forEach {
            channel.send("data-$it")
        }

        channel.close()

        channel.send("invalid")
    }

    runBlocking {
        repeat(3) {
            val s = channel.receive()
            println("step1 : $s")
        }
    }

    runBlocking {
        for (s in channel) {
            println("step2 : $s")
        }
    }
}