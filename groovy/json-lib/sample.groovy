
import net.sf.json.*

class Customer {
	String id
	String name
}

println JSONSerializer.toJSON([customers:
	[
		new Customer(id: "id-1", name: "test"),
		new Customer(id: "id-2", name: "テストデータ")
	]
]).toString()

println JSONSerializer.toJSON(new Customer(name: "aaa")).toString()
println JSONObject.fromObject(new Customer(id: "a:1", name: "b")).toString()

