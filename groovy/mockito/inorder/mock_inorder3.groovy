@Grab('org.mockito:mockito-core:2.0.31-beta')
import static org.mockito.Mockito.*

import org.mockito.ArgumentMatcher

import groovy.transform.*

@Immutable
class Data {
	int value
}

class SampleService {
	Data next(Data d) {
		new Data(d.value + 1)
	}
}

class A {
	SampleService service

	Data run(Data d) {
		service.next(
			service.next(d)
		)
	}
}

def a = new A(service: new SampleService())

println a.run(new Data(1))

// -----------------

def m2 = mock(SampleService)

when(m2.next(new Data(1))).thenReturn(new Data(2))
when(m2.next(new Data(2))).thenReturn(new Data(3))

def a2 = new A(service: m2)

println a2.run(new Data(1))

def order = inOrder(m2)

order.verify(m2).next(new Data(1))
order.verify(m2).next(new Data(2))

// -----------------

def matchSample = { int v ->
	// d が null になる場合がある
	{ d -> d != null && d.value == v } as ArgumentMatcher<Data>
}

println matchSample(1).matches(new Data(1))
println matchSample(1).matches(new Data(2))

def m3 = mock(SampleService)

when(m3.next(argThat(matchSample(1)))).thenReturn(new Data(2))
when(m3.next(argThat(matchSample(2)))).thenReturn(new Data(3))

def a3 = new A(service: m3)

println a3.run(new Data(1))

def order3 = inOrder(m3)

order3.verify(m3).next(argThat(matchSample(1)))
order3.verify(m3).next(argThat(matchSample(2)))
