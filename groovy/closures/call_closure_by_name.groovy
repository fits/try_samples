
toSnake = {
	it.replaceAll(/([A-Z]+)/, /_$1/).replaceAll(/^_/, '')
}

id = { it }

toUpper = { it.toUpperCase() }

appendStr = { "${it}!!!" }

println this."toSnake".call("sampleName")

def name = "toSnake"

println this[name].call("testName")


def func1 = [toSnake, toUpper, appendStr].inject { acc, val ->
	acc >> val
}
println func1.call("testSampleResult")


def func2 = ["toSnake", "toUpper", "appendStr"].inject(id) { acc, val ->
	acc >> this[val]
}
println func2.call("testSampleResult")
