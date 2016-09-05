
import java.io.ByteArrayOutputStream

import sample.model.addressbook.Person
import Person.PhoneNumber
import Person.PhoneType._

object SampleApp extends App {

	val phone = PhoneNumber("000-1234-5678", HOME)
	val person = Person(name = "sample1", phone = Seq(phone))

	println(person)

	val output = new ByteArrayOutputStream()

	try {
		person.writeTo(output)

		println("----------")

		val restoredPerson = Person.parseFrom(output.toByteArray)

		println(restoredPerson)

	} finally {
		output.close
	}
}
