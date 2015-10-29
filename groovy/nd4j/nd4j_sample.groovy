@Grab('org.nd4j:nd4j-java:0.4-rc3.5')
@Grab('org.slf4j:slf4j-nop:1.7.12')
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms

def d = Nd4j.arange(0.0, 5.0)

println d

println d.add(3)

println Transforms.sigmoid(d, false)
