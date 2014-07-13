
for (i in 0 ..< 10) {
	println i

	if (i == 5) break
}

println '-----'

(0 ..< 10).each {
	println it

	// each で break は使えない
	// if (it == 5) break
}
