package main

func contains[A comparable](vs []A, t A) bool {
	for _, v := range vs {
		if v == t {
			return true
		}
	}

	return false
}

func main() {
	println(contains([]string{"a", "b", "c"}, "b"))
	println(contains([]string{"a", "b", "c"}, "d"))

	println(contains([]int{1, 2, 3}, 3))
	
	// println(contains([]int{1, 2, 3}, "a")) // build error
}