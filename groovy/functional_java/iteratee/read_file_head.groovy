@Grab('org.functionaljava:functionaljava:3.1')
import fj.F
import fj.data.IO
import fj.data.Option
import static fj.data.Iteratee.*

import java.nio.charset.Charset

def iter = IterV.drop(1).bind({ IterV.head() } as F)

def ioIter = IO.enumFileLines(new File(args[0]), Option.some(Charset.defaultCharset()), iter)

println ioIter.run().run()
