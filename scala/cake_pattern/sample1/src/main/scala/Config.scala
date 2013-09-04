
object Config extends SampleDaoImplComponent with SampleServiceImplComponent {
	val dao = new SampleDaoImpl
	val service = new SampleServiceImpl
}