
import java.time.LocalDateTime

@main def main(): Unit =
  checkCargo()
  checkDelivery()

def now: LocalDateTime = LocalDateTime.now()
def nextDay(n: Long): LocalDateTime = now.plusDays(n)

def checkCargo(): Unit =
  import models.cargo.*
  import Cargo.*

  val d1 = create("id-1", "USNYC", "JNTKO", nextDay(10))

  val r1 = d1.foldMap(interpret).run(emptyCargo).value
  println(r1)

  val d2 = for {
    ev1 <- create("id-2", "USNYC", "JNTKO", nextDay(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", nextDay(1)), LocationTime("CNSHA", nextDay(4))),
        Leg("0200A", LocationTime("CNSHA", nextDay(5)), LocationTime("JNTKO", nextDay(7)))
      ))
    )
    b1 <- isOnRoute("CNSHA", None)
    b2 <- isDestination("JNTKO")
    ev3 <- close()
    b3 <- isOnRoute("JNTKO", None)
  } yield (ev1 ++ ev2 ++ ev3, b1, b2, b3)

  val r2 = d2.foldMap(interpret).run(emptyCargo).value
  println(r2)

  val d3 = for {
    ev1 <- create("id-3", "USNYC", "JNTKO", nextDay(10))
    ev2 <- assignRoute(Itinerary(List.empty))
  } yield ev1 ++ ev2

  val r3 = d3.foldMap(interpret).run(emptyCargo).value
  println(r3)

  val d4 = for {
    ev1 <- create("id-4", "USNYC", "JNTKO", nextDay(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", nextDay(1)), LocationTime("CNSHA", nextDay(4))),
        Leg("0200A", LocationTime("CNSHA", nextDay(5)), LocationTime("JNTKO", nextDay(7)))
      ))
    )
  } yield ev1 ++ ev2

  val r4 = d4.foldMap(interpret).run(emptyCargo).value
  println(r4)

  val d5 = for {
    ev1 <- create("id-5", "USNYC", "JNTKO", nextDay(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", nextDay(1)), LocationTime("CNSHA", nextDay(4))),
        Leg("0200A", LocationTime("CNSHA", nextDay(5)), LocationTime("AUMEL", nextDay(7)))
      ))
    )
  } yield ev1 ++ ev2

  val r5 = d5.foldMap(interpret).run(emptyCargo).value
  println(r5)

  val d6 = for {
    ev1 <- create("id-6", "USNYC", "JNTKO", nextDay(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", nextDay(1)), LocationTime("CNSHA", nextDay(4))),
        Leg("0200A", LocationTime("CNSHA", nextDay(5)), LocationTime("AUMEL", nextDay(7)))
      ))
    )
    ev3 <- changeDestination("AUMEL")
  } yield ev1 ++ ev2 ++ ev3

  val r6 = d6.foldMap(interpret).run(emptyCargo).value
  println(r6)

  val d7 = for {
    ev1 <- create("id-7", "USNYC", "JNTKO", nextDay(10))
    ev2 <- changeDeadline(now)
  } yield ev1 ++ ev2

  val r7 = d7.foldMap(interpret).run(emptyCargo).value
  println(r7)

end checkCargo

def checkDelivery(): Unit =
  import models.delivery.*
  import Delivery.*

  val d1 = for {
    e1 <- receive("USNYC", now)
    e2 <- load("0100S", nextDay(1))
    e3 <- unload("JNTKO", nextDay(4))
    e4 <- claim(nextDay(4))
  } yield e1 ++ e2 ++ e3 ++ e4

  val r1 = d1.foldMap(interpret).run(createDelivery("t1")).value
  println(r1)

  val d2 = for {
    e1 <- receive("USNYC", now)
    e2 <- load("0100S", nextDay(1))
    e3 <- unload("AUMEL", nextDay(4))
  } yield e1 ++ e2 ++ e3

  val r2 = d2.foldMap(interpret).run(createDelivery("t2")).value
  println(r2)

end checkDelivery

