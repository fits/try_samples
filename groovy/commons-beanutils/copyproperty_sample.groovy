@Grab('commons-beanutils:commons-beanutils:1.8.3')
import org.apache.commons.beanutils.BeanUtils

class Item {
	int id
	String name
	String data

	@Override String toString() {
		"id: ${id}, name: ${name}, data: ${data}"
	}
}

def a = new Item(id: 1, name: 'test1', data: 'aaa')
def b = new Item(id: 2, name: 'test2', data: 'bb')

println a
println b

println "-----"

// copyProperty は 2つの Bean の指定プロパティをコピーするわけでは無い
BeanUtils.copyProperty(b, 'data', 'abc')

println a
println b

println "-----"

BeanUtils.setProperty(b, 'data', BeanUtils.getProperty(a, 'data'))

println a
println b

println "-----"

BeanUtils.copyProperties(b, a)

println a
println b

