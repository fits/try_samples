
trait SampleDaoComponent {
	val dao: SampleDao

	trait SampleDao {
		def save(d: Data): Unit
	}
}