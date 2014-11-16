
import groovy.json.JsonBuilder

def builder = new JsonBuilder()

builder.call([a: 1])
// {"a":1}
println builder.toString()

builder.call([b: 2])
// {"b":2}
println builder.toString()
