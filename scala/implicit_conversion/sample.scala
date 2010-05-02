
implicit def itos(i: Int) = {
	println("itos : " + i)
	i.toString
}

implicit val itos2 = (i: Int) => {
	println("itos2 : " + i)
	i.toString
}

def test(s: String) = {
	println("println = " + s)
}

test(100)
