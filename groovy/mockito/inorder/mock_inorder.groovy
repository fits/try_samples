@Grab('org.mockito:mockito-core:2.0.31-beta')
import static org.mockito.Mockito.*

class SampleService {
	void sampleA() {
		println 'call SampleService.sampleA()'
	}

	void sampleB() {
		println 'call SampleService.sampleB()'
	}
}

class A {
	SampleService service

	void call() {
		service.sampleA()
		service.sampleB()
	}
}

def a = new A(service: new SampleService())

a.call()

def m = mock(SampleService)

def a2 = new A(service: m)

a2.call()

def order = inOrder(m)

order.verify(m).sampleA()
order.verify(m).sampleB()
