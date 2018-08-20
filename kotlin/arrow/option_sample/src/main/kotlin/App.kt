
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    val d1: Option<Int> = Option.just(10)
    val d2: Option<Int> = 5.some()
    val d3: Option<Int> = Some(2)
    val d4: Option<Int> = none()

    val r1 = d1.flatMap { a ->
        d2.map { b -> a + b }
    }

    println(r1)

    val r2 = Option.monad().binding {
        val a = d1.bind()
        val b = d2.bind()
        val c = d3.bind()
        a + b + c
    }

    println(r2)

    val r3 = Option.monad().binding {
        val a = d1.bind()
        val b = d4.bind()
        a + b
    }

    println(r3)
}