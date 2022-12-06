import java.time.LocalDateTime

def now: LocalDateTime = LocalDateTime.now()
def nextDay(n: Long): LocalDateTime = now.plusDays(n)

object Main:
  def main(args: Array[String]): Unit = 
    import models.*
    import Cargo.*

    val d = for {
      ev1 <- create("id-1", "USNYC", "JNTKO", nextDay(10))
      ev2 <- assignRoute(
        Itinerary(List(
          Leg("0100S", LocationTime("USNYC", nextDay(1)), LocationTime("CNSHA", nextDay(4))),
          Leg("0200A", LocationTime("CNSHA", nextDay(5)), LocationTime("JNTKO", nextDay(7)))
        ))
      )
      ev3 <- close()
    } yield ev1 ++ ev2 ++ ev3

    val (state, events) = d.foldMap(interpret).run(emptyCargo).value

    println("--- state ---")
    println(state)

    println("--- events ---")
    events.foreach(println)
