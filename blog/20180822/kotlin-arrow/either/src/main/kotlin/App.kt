
import arrow.core.*
import arrow.instances.*
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    // Right
    val d1: Either<String, Int> = Either.right(10)
    val d2: Either<String, Int> = 5.right()
    val d3: Either<String, Int> = Right(2)
    // Left
    val d4: Either<String, Int> = Either.left("error data")

    // Right(b=15)
    val r1 = d1.flatMap { a ->
        d2.map { b -> a + b }
    }

    println(r1)

    // Right(b=17)
    val r2 = Either.monad<String>().binding {
        val a = d1.bind()
        val b = d2.bind()
        val c = d3.bind()
        a + b + c
    }

    println(r2)

    // Right(b=17)
    val r3 = ForEither<String>() extensions {
        binding {
            val a = d1.bind()
            val b = d2.bind()
            val c = d3.bind()
            a + b + c
        }
    }

    println(r3)

    // Left(a=error data)
    val r4 = ForEither<String>() extensions {
        binding {
            val a = d1.bind()
            val b = d4.bind()
            val c = d3.bind()
            a + b + c
        }
    }

    println(r4)
}