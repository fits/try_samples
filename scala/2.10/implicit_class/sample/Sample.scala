package fits.sample

object Sample extends App {

	implicit class SampleString(str: String) {
		def sample: String = {
			str + "!"
		}
	}

	println("test".sample)
}
