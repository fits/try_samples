
trait SampleDaoImplComponent extends SampleDaoComponent {
	class SampleDaoImpl extends SampleDao {
		def save(d: Data): Unit = {
			println(s"save : ${d}")
		}
	}
}