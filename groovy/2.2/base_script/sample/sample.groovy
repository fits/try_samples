
import groovy.transform.BaseScript

@BaseScript SampleScript baseScript

println sample1('aaa')

println '-----'

println baseScript.dump()

println '-----'

println baseScript.metaClass.methods
