@Grab('org.easybatch:easybatch-core:4.0.0')
@Grab('org.apache.commons:commons-lang3:3.4')
import org.easybatch.core.job.JobBuilder
import org.easybatch.core.reader.StringRecordReader
import org.easybatch.core.record.GenericRecord
import org.easybatch.core.processor.ComputationalRecordProcessor
import org.apache.commons.lang3.math.NumberUtils

def data = new StringRecordReader('''
1
5
50
10
50
1000
5
10
100
500
1000
2000
100
1
5
1000
10
1000
''')

class Counter implements ComputationalRecordProcessor {
	private def counter = [:]

	def getComputationResult() {
		counter
	}

	def processRecord(r) {
		if (!counter[r.payload]) {
			counter[r.payload] = 0
		}

		counter[r.payload]++
		r
	}
}

def res = JobBuilder.aNewJob()
	.reader(data)
	.filter { NumberUtils.isNumber(it.payload)? it: null }
	.mapper { new GenericRecord(it.header, it.payload as int) }
	.processor(new Counter())
	.build()
	.call()

println res.result
