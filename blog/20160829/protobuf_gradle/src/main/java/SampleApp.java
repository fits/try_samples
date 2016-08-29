
import static sample.model.AddressBookProtos.Person.PhoneType.*;

import lombok.val;

import java.io.ByteArrayOutputStream;

import sample.model.AddressBookProtos.Person;
import sample.model.AddressBookProtos.Person.PhoneNumber;

class SampleApp {
	public static void main(String... args) throws Exception {

		val phone = PhoneNumber.newBuilder()
						.setNumber("000-1234-5678")
						.setType(HOME)
						.build();

		val person = Person.newBuilder()
						.setName("sample1")
						.addPhone(phone)
						.build();

		System.out.println(person);

		try (val output = new ByteArrayOutputStream()) {

			person.writeTo(output);

			System.out.println("----------");

			val restoredPerson = Person.newBuilder()
									.mergeFrom(output.toByteArray())
									.build();

			System.out.println(restoredPerson);
		}
	}
}