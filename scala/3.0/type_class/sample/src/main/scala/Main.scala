
trait Value[D, V]:
  extension (d: D)
    def value: V

case class Data(name: String)

object Value:
  given Value[String, Int] with
    extension (s: String)
      def value: Int = s.length

  given Value[Data, String] with
    extension (d: Data)
      def value: String = d.name

@main def main: Unit = 
  import Value.given

  println("sample1".value)

  val d = Data("sample2")
  println(s"value: ${d.value}")
