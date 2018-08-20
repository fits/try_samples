
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    val d1: Either<String, Int> = Either.right(10)
    val d2: Either<String, Int> = Either.right(5)
    val d3: Either<String, Int> = Either.left("error")

    val r1 = d1.flatMap { a ->
        d2.map { b -> a + b}
    }

    println(r1)

    val r2 = d1.flatMap { a ->
        d3.map { b -> a + b }
    }

    println(r2)

    val r3 = ForEither<String>() extensions {
        binding {
            val a = Either.right(10).bind()
            val b = Either.right(5).bind()
            a + b
        }
    }

    println(r3)
}