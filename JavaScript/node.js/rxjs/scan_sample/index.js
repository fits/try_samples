
const Rx = require('rxjs')

const subject = new Rx.Subject()

subject.scan((acc, d) => acc + d, 0)
		.subscribe(d => console.log(`scan value: ${d}`))

subject.reduce((acc, d) => acc + d, 0)
		.subscribe(d => console.log(`reduce value: ${d}`))

subject.next(1)
subject.next(2)
subject.next(3)
subject.next(4)
subject.next(5)

subject.complete()
