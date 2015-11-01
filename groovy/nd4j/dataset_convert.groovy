@Grab('org.nd4j:nd4j-java:0.4-rc3.5')
@Grab('org.slf4j:slf4j-nop:1.7.12')
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

def SIZE = 8

def d = new DataSet(
	Nd4j.create([1, 3, 5] as float[]), 
	Nd4j.create([2] as float[])
)

def list = []

d.labels.each { label ->
	(0..<SIZE).each { n ->
		def tmp = d.copy()
		def v = (n < label)? 1: 0

		tmp.setLabels(Nd4j.create([v] as float[]))

		list << tmp
	}
}

println DataSet.merge(list)
