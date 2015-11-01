@Grab('org.nd4j:nd4j-java:0.4-rc3.5')
@Grab('org.slf4j:slf4j-nop:1.7.12')
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j

def d = new DataSet(
	Nd4j.create([1, 3, 5] as float[]), 
	Nd4j.create([2] as float[])
)

println d
