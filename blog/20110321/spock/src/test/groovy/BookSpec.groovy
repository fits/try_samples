package fits.sample

import spock.lang.*

class InitBookSpec extends Specification {
	def b = new Book()

	def "comments ‚Í null ‚Å‚Í‚È‚¢"() {
		expect:
			b.comments != null
	}

	def "comments ‚Í‹ó"() {
		expect:
			b.comments.size == 0
	}
}

class AddCommentSpec extends Specification {
	def b = new Book()

	def "Comment ‚ð’Ç‰Á"() {
		when:
			b.comments.add(new Comment())
		then:
			b.comments.size == 1
	}
}
