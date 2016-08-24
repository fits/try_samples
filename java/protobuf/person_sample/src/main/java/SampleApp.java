
import static sample.model.AddressBookProtos.Person.PhoneType.*;

import lombok.val;

import java.io.*;

import sample.model.AddressBookProtos.Person;
import sample.model.AddressBookProtos.Person.PhoneNumber;

class SampleApp {
	public static void main(String... args) throws Exception {

		val phoneBuilder = PhoneNumber.newBuilder();
		phoneBuilder.setNumber("111-000-000");
		phoneBuilder.setType(HOME);

		val personBuilder = Person.newBuilder();
		personBuilder.setName("sample1");
		personBuilder.addPhone(phoneBuilder.build());

		val person = personBuilder.build();

		System.out.println(person);

		try (val output = new ByteArrayOutputStream()) {

			person.writeTo(output);

			val personBuilder2 = Person.newBuilder();

			System.out.println("--- read ---");

			try (val input = new ByteArrayInputStream(output.toByteArray())) {

				personBuilder2.mergeFrom(input);

				System.out.println(personBuilder2.build());
			}
		}
	}
}