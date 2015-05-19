@Grab('com.esotericsoftware:kryo:3.0.1')
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class Data {
	String name
	int value
	List<DataItem> items = []
}

class DataItem {
	String code
	int num
}

def d = new Data(
	name: 'sample1',
	value: 1,
	items: [
		new DataItem(code: 'C1', num: 1),
		new DataItem(code: 'D2', num: 2)
	]
)

println d

Kryo kryo = new Kryo()

def output = new Output(10000, -1)

kryo.writeObject(output, d)

output.close()

def res = output.toBytes()

println res.length

def input = new Input(res)

def d2 = kryo.readObject(input, Data)

input.close()

println d2.dump()
