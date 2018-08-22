
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    // Some
    val d1: Option<Int> = Option.just(10)
    val d2: Option<Int> = 5.some()
    val d3: Option<Int> = Some(2)
    // None
    val d4: Option<Int> = Option.empty()
    val d5: Option<Int> = none()
    val d6: Option<Int> = None

    // Some(15)
    val r1 = d1.flatMap { a ->
        d2.map { b -> a + b }
    }

    println(r1)

    // Some(17)
    val r2 = Option.monad().binding {
        val a = d1.bind()
        val b = d2.bind()
        val c = d3.bind()
        a + b + c
    }

    println(r2)

    // Some(17)
    val r3 = ForOption extensions {
        println(this)

        binding {
            println(this)

            val a = d1.bind()
            val b = d2.bind()
            val c = d3.bind()
            a + b + c
        }
    }

    println(r3)

    // Some(17)
    val r4 = OptionContext.binding {
        val a = d1.bind()
        val b = d2.bind()
        val c = d3.bind()
        a + b + c
    }

    println(r4)

    // None
    val r5 = Option.monad().binding {
        val a = d1.bind()
        val b = d4.bind()
        a + b
    }

    println(r5)

    // None
    val r6 = ForOption extensions {
        binding {
            val a = d5.bind()
            val b = d2.bind()
            val c = d6.bind()
            a + b + c
        }
    }

    println(r6)
}