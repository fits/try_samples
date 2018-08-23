package sample

package object es {
  import cats.free.Free

  type Command[A] = Free[Event, A]

  trait Event[A]
}