@Grab('org.apache.lucene:lucene-core:6.2.1')
import org.apache.lucene.util.*

def v = 123456

println '--- LegacyNumericUtils ---'

BytesRefBuilder lbuf = new BytesRefBuilder()

LegacyNumericUtils.intToPrefixCoded(v, 0, lbuf)

println "intToPrefixCoded=${lbuf.get().bytes}"
println "prefixCodedToInt=${LegacyNumericUtils.prefixCodedToInt(lbuf.get())}"

println ''
println '--- NumericUtils ---'

def nbuf = new byte[Integer.BYTES]

NumericUtils.intToSortableBytes(v, nbuf, 0)

println "intToSortableBytes=${nbuf}"
println "sortableBytesToInt=${NumericUtils.sortableBytesToInt(nbuf, 0)}"

