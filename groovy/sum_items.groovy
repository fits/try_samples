
def list = [
	[id: 1, value: 1],
	[id: 2, value: 3],
	[id: 3, value: 5]
]

// 9
println list*.value.sum()
// 9
println list.value.sum()
// 9
println list.sum { it.value }

// [1, 3, 5]
println list*.value
// [1, 3, 5]
println list.value
