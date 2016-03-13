@Grab('org.easybatch:easybatch-core:4.0.0')
import org.easybatch.core.job.JobBuilder
import org.easybatch.core.reader.StringRecordReader
import org.easybatch.core.record.GenericRecord
import org.easybatch.core.writer.StandardOutputRecordWriter

def data = new StringRecordReader('''a
bc
def
ghij
''')

def job = JobBuilder.aNewJob()
	.reader(data)
	.mapper { r -> new GenericRecord(r.header, r.payload.trim().length()) }
	.writer(new StandardOutputRecordWriter())
	.build()

def res = job.call()

println job
println res
