
trait SampleServiceImplComponent extends SampleServiceComponent { this: SampleDaoComponent =>
	class SampleServiceImpl extends SampleService {
		def call: Unit = {
			println("*** call")
			dao.save(Data("1", "test"))
		}
	}
}