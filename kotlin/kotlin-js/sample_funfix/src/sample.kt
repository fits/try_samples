
fun funfix() = js("require('funfix')")

external interface Option<T> {
    fun <S> flatMap(func: (T) -> Option<S>): Option<S>
    fun <S> map(func: (T) -> S): Option<S>
    fun getOrElse(v: T): T
}

fun f(d1: Option<Int>, d2: Option<Int>): Option<String> = d1.flatMap { a -> 
    d2.map { b ->
        "$a + $b = ${a + b}"
    }
}

fun main(args: Array<String>) {
    val funfix = funfix()

    println( funfix.Some(10).getOrElse(-1) )

    val r1 = funfix.Some(10).flatMap { a ->
        funfix.Some(2).map { b ->
            a + b
        }
    }

    println("r1 = ${r1.getOrElse("none")}")

    val r2 = funfix.Some(10).flatMap { a -> 
        funfix.Option.none().map { b -> 
            a + b
        }
    }

    println("r2 = ${r2.getOrElse(-1)}")


    val r3 = f(funfix.Some(3), funfix.Some(4))

    println("r3 : ${r3.getOrElse("none")}")

    val r4 = f(funfix.Some(3), funfix.Option.none())

    println("r4 : ${r4.getOrElse("none")}")
}