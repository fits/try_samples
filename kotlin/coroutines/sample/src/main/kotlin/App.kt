import kotlinx.coroutines.*

fun main() {
    sample1()
    sample2()
}

fun sample1() {
    val ds = (1..10).map { "data-${it}" }

    println("--- sample1-1 ---")

    runBlocking {
        ds.forEach {
            println(showData("sample1-1", it))
        }
    }

    println("--- sample1-2 ---")

    runBlocking {
        ds.forEach {
            async { println(showData("sample1-2", it)) }
        }
    }

    println("--- sample1-3 ---")

    val test3 = GlobalScope.launch {
        ds.forEach {
            println(showData("sample1-3", it))
        }
    }

    runBlocking {
        test3.join()
    }
}

suspend fun showData(prefix: String, s: String): String {
    delay(Math.round(Math.random() * 100))
    return "${prefix}:${s}"
}


fun sample2() {
    runBlocking {
        println(showData2("sample2-1", 100))
    }

    GlobalScope.launch {
        println(showData2("sample2-2", 100))
    }

    val res = GlobalScope.launch {
        println(showData2("sample2-3", 50))
    }

    runBlocking {
        res.join()
    }
}

suspend fun showData2(s: String, waitTime: Long): String {
    delay(waitTime)
    return s
}
