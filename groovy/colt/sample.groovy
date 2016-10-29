@Grab('com.blazegraph:colt:2.1.4')
import cern.colt.matrix.DoubleFactory2D
import cern.colt.matrix.linalg.Algebra
import cern.jet.math.Functions

def d1 = DoubleFactory2D.dense.make([[1, 2], [3, 4]] as double[][])

println d1

def d2 = DoubleFactory2D.dense.make([[5, 6], [7, 8]] as double[][])

println d2

println '-----'

// d1 + d2
println d1.copy().assign(d2, Functions.plus)

// d1 = d1 + d2
//println d1.assign(d2, Functions.plus)

println '-----'

def algebra = new Algebra()

// d1 * d2
println algebra.mult(d1, d2)
