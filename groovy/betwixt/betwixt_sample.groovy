@Grapes([
@Grab('commons-betwixt:commons-betwixt:0.8'),
@GrabExclude('commons-beanutils#commons-beanutils'),
@GrabExclude('commons-collections#commons-collections')
])
import org.apache.commons.betwixt.io.*

class SampleData {
	String name
	BigDecimal price
}

def sw = new StringWriter()

def writer = new BeanWriter(sw)
//以下の指定が無いと id 属性が自動的に連番で出力される
writer.writeIDs = false

writer.write([
	new SampleData(name: "test", price: 100),
	new SampleData(name: "test2", price: 0)
])

writer.close()

def data = sw.toString()
println data
println "----------------------"

def reader = new BeanReader()
//ArrayList の登録が必要
reader.registerBeanClass(ArrayList)
reader.registerBeanClass(SampleData)

reader.parse(new StringReader(data)).each {
	println it.dump()
}

