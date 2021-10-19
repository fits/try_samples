
trait Context[A]:
  def show(v: A): Unit

def sample(v: String)(using ctx: Context[String]) =
  ctx.show(v + "!!")

def sample(using ctx: Context[Int])(v: Int) =
  ctx.show(v * 10)

@main def main: Unit = 
  given Context[String] = new Context[String]:
    def show(v: String): Unit = println(s"string context: $v")

  given c: Context[Int] = new Context[Int]:
    def show(v: Int): Unit = println(s"int context: $v")

  sample("test1")
  sample(5)
