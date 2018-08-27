
const monet = require('monet')
const Free = monet.Free
const Identity = monet.Identity

const d1 = Free.liftF(Identity('a'))
const d2 = Free.liftF(Identity('b'))

const proc = d1.flatMap(a => d2.map(b => a + b))

console.log(proc)

proc.go(d => {
	console.log(JSON.stringify(d))
	return d.get()
})

