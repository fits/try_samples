@Grab('com.esotericsoftware:kryo:4.0.0')
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

import groovy.transform.*

@Immutable
class Data {
	String name
	int value
}

@Immutable
class Data2 {
	String a
	int b
}

@Immutable
class Data3 {
	String a
	long b
}

@Immutable
class Data4 {
	String a
}

@Immutable
class Data5 {
	String a
	int b
	int c
}


def kryo = new Kryo()

def output = new Output(1024)

kryo.writeObject(output, new Data('sample1', 10))

output.close()

def buf = output.toBytes()

println new String(buf)

def dumpReadObj = { b, cls ->
	def input = new Input(b)

	println kryo.readObject(input, cls)

	input.close()
}

dumpReadObj(buf, Data)
dumpReadObj(buf, Data2)
dumpReadObj(buf, Data3)
dumpReadObj(buf, Data4)
dumpReadObj(buf, Data5)
