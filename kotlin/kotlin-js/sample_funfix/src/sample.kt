
fun funfix() = js("require('funfix')")

fun main(args: Array<String>) {
    val funfix = funfix()

    println( funfix.Some(10).getOrElse("none") )

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

    println("r2 = ${r2.getOrElse("none")}")
}