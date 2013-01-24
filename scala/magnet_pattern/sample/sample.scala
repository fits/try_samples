
trait Sample {
	type Result

	def exec: Result
}

def f(obj: Sample): obj.Result = obj.exec

case class StringSample(value: String) extends Sample {
	type Result = String

	def exec: Result = value
}

case class IntSample(value: Int) extends Sample {
	type Result = Int

	def exec: Result = value
}

println( f(StringSample("test")) )
println( f(IntSample(123)) )

