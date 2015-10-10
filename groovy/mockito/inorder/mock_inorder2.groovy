@Grab('org.mockito:mockito-core:2.0.31-beta')
import static org.mockito.Mockito.*

class SampleService {
	int sampleA(int num) {
		num + 1
	}
}

class A {
	SampleService service

	int call() {
		service.sampleA(
			service.sampleA(1)
		)
	}
}

def a = new A(service: new SampleService())

println a.call()

def m = mock(SampleService)

when(m.sampleA(1)).thenReturn(2)
when(m.sampleA(2)).thenReturn(3)

def a2 = new A(service: m)

println a2.call()

def order = inOrder(m)

order.verify(m).sampleA(1)
order.verify(m).sampleA(2)
