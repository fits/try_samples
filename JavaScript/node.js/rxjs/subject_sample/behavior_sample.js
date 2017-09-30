
const Rx = require('rxjs')

const subject = new Rx.BehaviorSubject(0)

subject.subscribe(v => console.log(`subject1: ${v}`))
subject.subscribe(v => console.log(`subject2: ${v}`))

subject.next(1)

subject.subscribe(v => console.log(`subject3: ${v}`))

subject.next(2)

subject.complete()
