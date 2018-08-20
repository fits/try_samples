
import arrow.core.*
import arrow.data.Kleisli
import arrow.typeclasses.binding

fun main(args: Array<String>) {
    val f1: Kleisli<ForOption, Int, Int> = Kleisli { a -> Option.just(a + 3) }
    val f2: Kleisli<ForOption, Int, Int> = Kleisli { a -> Some(a * 2) }

    val f3: Kleisli<ForOption, Int, Int> = Kleisli { _ -> Option.empty() }

    val f4 = f1.andThen(Option.monad(), f2)
    val f5 = f1.andThen(Option.monad(), f3)

    println( f1.run(4) )
    println( f2.run(4) )
    println( f3.run(4) )
    println( f4.run(4) )
    println( f5.run(4) )

    val f6 = { a: Int ->
        Option.monad().binding {
            val b = f1.run(a).bind()
            val c = f2.run(b).bind()
            c
        }
    }

    println( f6(4) )
}