import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.getOrElse

import java.lang.Exception
import java.util.UUID

typealias AssignId = String
typealias AssignError = Throwable

fun assign(num: Int) =
        if (num in 1..10) Either.right("assign:${UUID.randomUUID().toString()}")
        else Either.left(Exception("invalid num : $num"))

fun main() {
    val r1 = Either.fx<AssignError, List<AssignId>> {
        val (a) = assign(1)
        val (b) = assign(3)

        listOf(a, b)
    }

    println(r1)

    val r1a = r1.flatMap { assign(10) }
    println(r1a)

    println(r1.flatMap { assign(11) })

    val r2 = Either.fx<AssignError, List<AssignId>> {
        val (a) = assign(-1)
        val (b) = assign(3)

        listOf(a, b)
    }

    println(r2)
    println(r2.getOrElse { listOf() })
}

