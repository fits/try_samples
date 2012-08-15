
class Customer {
	String name
	//addresses では無く クラス名 + 's' としなければ
	//ObjectGraphBuilder で処理されない
	List<Address> addresss = []
}

class Address {
	String city
}


def builder = new ObjectGraphBuilder()

def c1 = builder.customer(name: 'テストユーザー') {
	address(city: '東京')
	address(city: '大阪')
}

println c1.dump()
// <Customer@aeba3ff name=テストユーザー addresss=[Address@5c508d73, Address@2c76a85e]>
