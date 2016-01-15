@Grab('org.la4j:la4j:0.6.0')
import static org.la4j.LinearAlgebra.InverterFactory.*
import org.la4j.Matrix

def m1 = Matrix.from2DArray([[4, 1], [3, 2]] as double[][])

println m1.determinant()
println m1.trace()

println ''

println m1.transpose()

println m1.withInverter(SMART).inverse()
