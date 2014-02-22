@Grab('commons-beanutils:commons-beanutils:1.9.1')
import org.apache.commons.beanutils.*

class Data {
	String id
	List<DataItem> items
}

class DataItem {
	String itemId
	int value
}

def d = new Data(id: 'data1', items: [
	new DataItem(itemId: 'item1', value: 1),
	new DataItem(itemId: 'item2', value: 2),
	new DataItem(itemId: 'item3', value: 3)
])

def m = new BeanMap(d)

println m

m.items.each {
	println it
}

println PropertyUtils.getProperty(d, 'items[0].itemId')

println '-------------------'

PropertyUtils.getPropertyDescriptors(d).each {
	println it.name
}
