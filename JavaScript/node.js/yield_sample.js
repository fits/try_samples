
function* g1() {
    yield 11
    yield 12
}

function* g2() {
	yield 21
	yield* g1()
	yield 22
}

for (const d of g2()) {
	console.log(d)
}

const it = g2()

console.log(it.next())
console.log(it.next())
console.log(it.next())
console.log(it.next())
console.log(it.next())
