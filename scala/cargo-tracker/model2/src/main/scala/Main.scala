
import java.time.LocalDateTime
import models.cargo.{Itinerary, Leg, LocationTime}
import models.cargo.CargoAction._

@main def main(): Unit =
  val now = LocalDateTime.now()

  val d1 = create("id-1", "USNYC", "JNTKO", now.plusDays(10))

  val r1 = d1.foldMap(interpret).run(emptyCargo).value
  println(r1)

  val d2 = for {
    ev1 <- create("id-2", "USNYC", "JNTKO", now.plusDays(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("JNTKO", now.plusDays(7)))
      ))
    )
    ev3 <- close()
  } yield ev1 ++ ev2 ++ ev3

  val r2 = d2.foldMap(interpret).run(emptyCargo).value
  println(r2)

  val d3 = for {
    ev1 <- create("id-3", "USNYC", "JNTKO", LocalDateTime.now().plusDays(10))
    ev2 <- assignRoute(Itinerary(List.empty))
  } yield ev1 ++ ev2

  val r3 = d3.foldMap(interpret).run(emptyCargo).value
  println(r3)

  val d4 = for {
    ev1 <- create("id-4", "USNYC", "JNTKO", now.plusDays(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("JNTKO", now.plusDays(7)))
      ))
    )
  } yield ev1 ++ ev2

  val r4 = d4.foldMap(interpret).run(emptyCargo).value
  println(r4)

  val d5 = for {
    ev1 <- create("id-5", "USNYC", "JNTKO", now.plusDays(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("AUMEL", now.plusDays(7)))
      ))
    )
  } yield ev1 ++ ev2

  val r5 = d5.foldMap(interpret).run(emptyCargo).value
  println(r5)

  val d6 = for {
    ev1 <- create("id-6", "USNYC", "JNTKO", now.plusDays(10))
    ev2 <- assignRoute(
      Itinerary(List(
        Leg("0100S", LocationTime("USNYC", now.plusDays(1)), LocationTime("CNSHA", now.plusDays(4))),
        Leg("0200A", LocationTime("CNSHA", now.plusDays(5)), LocationTime("AUMEL", now.plusDays(7)))
      ))
    )
    ev3 <- changeDestination("AUMEL")
  } yield ev1 ++ ev2 ++ ev3

  val r6 = d6.foldMap(interpret).run(emptyCargo).value
  println(r6)

  val d7 = for {
    ev1 <- create("id-7", "USNYC", "JNTKO", now.plusDays(10))
    ev2 <- changeDeadline(now)
  } yield ev1 ++ ev2

  val r7 = d7.foldMap(interpret).run(emptyCargo).value
  println(r7)
