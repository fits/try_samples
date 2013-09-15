@Grab('commons-beanutils:commons-beanutils-core:1.8.3')
import org.apache.commons.beanutils.*

def data = [
	'aaa' : 200,
	'bbb' : [
		'123' : 100
	]
]

println PropertyUtils.getNestedProperty(data, 'aaa') // 200
println PropertyUtils.getNestedProperty(data, 'bbb') // [123:100]
println PropertyUtils.getNestedProperty(data, 'bbb.123') // 100

println PropertyUtils.getNestedProperty(data, 'bbb.aaa') // null

