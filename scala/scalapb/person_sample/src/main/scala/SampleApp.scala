
import resource._
import java.io.ByteArrayOutputStream

import sample.model.person.Person
import Person.PhoneNumber
import Person.PhoneType._

object SampleApp extends App {

	val pnum = PhoneNumber("111-000-000", HOME)
	val p1 = Person(name = "sample", phone = Seq(pnum))

	println(p1)

	managed(new ByteArrayOutputStream()).acquireFor { output =>

		p1.writeTo(output)

		println("--- read ---")

		val p2 = Person.parseFrom(output.toByteArray)

		println(p2)
	}
}
