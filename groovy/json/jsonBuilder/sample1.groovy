
import groovy.json.JsonBuilder

def builder = new JsonBuilder()

builder {
	name 'sample'
	value 10
	on true
	items 'ch1', 'ch2'
}

// {"name":"sample","value":10,"on":true,"items":["ch1","ch2"]}
println builder.toString()

def builder2 = new JsonBuilder()

def d2 = [
	[name: 'a1', value: 11, on: true, items: ['ch1', 'ch2']],
	[name: 'b2', value: 22, on: false]
]

builder2.call(d2)

// [{"name":"a1","value":11,"on":true,"items":["ch1","ch2"]},{"name":"b2","value":22,"on":false}]
println builder2.toString()
