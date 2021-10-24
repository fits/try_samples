
case class Data(name: String)

extension (d: Data)
  def show = println(s"""Data(name: "${d.name}")""")

extension [A](a: => A)
  def when(b: Boolean) = if(b) a

@main def main: Unit = 
  Data("sample").show

  println("test true") when true
  println("test false") when false
