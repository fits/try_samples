val xml = <test>{
	for (i <- 1 to 5)
		yield <no>{
			i
		}</no>
}</test>

println(xml.mkString)