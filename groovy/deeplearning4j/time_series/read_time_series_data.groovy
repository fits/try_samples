@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.canova.api.records.reader.impl.CSVSequenceRecordReader
import org.canova.api.split.NumberedFileInputSplit
import org.deeplearning4j.datasets.canova.SequenceRecordReaderDataSetIterator

def reader = new CSVSequenceRecordReader()

reader.initialize(new NumberedFileInputSplit('data/sample%d.csv', 1, 3))

// regression
def iterator = new SequenceRecordReaderDataSetIterator(reader, 1, -1, 2, true)

iterator.each { println it }

