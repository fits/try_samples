
@Immutable class TestData {
	int number
	String name

	//def だとエラーが発生
	//def name

	/* コンストラクタ以外でプロパティは変更できない
	def changeName(newName) {
		name = newName
	}
	*/

}

def t1 = new TestData(10, "test")
def t2 = new TestData(number: 10, name: "test")

println t1 == t2

//リードオンリーのためプロパティは変更できない
//t2.name = "a"
