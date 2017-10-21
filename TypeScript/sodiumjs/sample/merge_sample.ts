
import { StreamSink, Transaction } from 'sodiumjs'

const s1 = new StreamSink<number>()
const s2 = new StreamSink<number>()

s1.merge(s2, (a, b) => a * b)
	.listen(d => console.log(`*** ${d}`))

// 6
Transaction.run<void>(() => {
	s1.send(2)
	s2.send(3)
})

// 4
s1.send(4)

// 7
s2.send(7)

// 90
Transaction.run<void>(() => {
	s1.send(10)
	s2.send(9)
})
