
trait SampleServiceComponent {
	val service: SampleService

	trait SampleService {
		def call: Unit
	}
}