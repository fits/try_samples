
enum Event:
  case Created
  case Added(val value: Int)

import Event.*

def restore(events: List[Event]): Int = events.foldLeft(0)((acc, ev) => ev match
  case Created => 0
  case Added(v) => acc + v
)

@main def sample: Unit = 
  val ev1 = Created
  val ev2 = Added(2)

  println(ev1)
  println(ev2)

  println(restore(List(ev1, ev2, Added(1))))
  println(restore(List(ev1, ev2, ev1, Added(1))))
