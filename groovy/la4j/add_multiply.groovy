@Grab('org.la4j:la4j:0.6.0')
import org.la4j.Matrix

def d1 = Matrix.from2DArray([[1, 2], [3, 4]] as double[][])

println d1

def d2 = Matrix.from2DArray([[5, 6], [7, 8]] as double[][])

println d2

println '-----'

// d1 + d2
println d1.add(d2)

println '-----'

// d1 * d2
println d1.multiply(d2)
