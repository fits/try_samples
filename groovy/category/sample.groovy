
class SampleCategory {
	static void sample(String self) {
		println "sample: $self"
	}
}

//"test1".sample()

use(SampleCategory) {
	"test2".sample()
}
