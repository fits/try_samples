package fits.sample

import spock.lang.*

class BookSpockTest extends Specification {
	def b = new Book()
	
	def "set title"() {
		when:
		b.title = "test"

		then:
			b.title == "test"
	}
}
