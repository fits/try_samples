
import groovy.json.JsonBuilder

def json = new JsonBuilder()

json (
	name: 'sample1',
	id: 1,
	aaa: {
		id 12
	}
)

println json.toString()

json {
	name 'sample2'
	id 2
	bbb {
		id 21
		name 'bbb'
	}
}


println json.toString()
