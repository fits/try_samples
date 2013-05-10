@Grab('org.functionaljava:functionaljava:3.1')
import fj.F
import fj.P1
import fj.data.IO
import fj.data.Option
import static fj.data.Iteratee.*

import java.nio.charset.Charset

def take(int n) {
	def step
	step = { int count, List acc, Input s ->
		def empty = { IterV.cont( { step(count, acc, it) } as F) } as P1
		def eof = { IterV.done(acc, Input.eof()) } as P1

		def el = {
			return {
				def value = s.apply(null, { return { it } as F } as P1, null)

				(count == 1)? IterV.done(acc << value, Input.empty()): IterV.cont({ step(count - 1, acc << value, it) } as F)
			} as F
		} as P1

		s.apply(empty, el, eof)
	}

	(n == 0)? IterV.done([], Input.empty()): IterV.cont({ step(n, [], it) } as F)
}

def iter = IterV.drop(1).bind({ take(2) } as F)

def ioIter = IO.enumFileLines(new File(args[0]), Option.some(Charset.defaultCharset()), iter)

def res = ioIter.run().run()

//println res

res.each {
	println it
}
