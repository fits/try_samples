
import groovy.transform.BaseScript

@BaseScript MnistLoader baseScript

def testData = loadMnist('t10k-images.idx3-ubyte', 't10k-labels.idx1-ubyte')

def printData = { d ->
	(0..<28).each { r ->
		(0..<28).each { c ->
			print(d.features.getInt(c + r * 28) > 0 ? '#' : ' ')
		}
		println ''
	}

	println d.labels
}


printData testData.get(0)
printData testData.get(2)
printData testData.get(4)
