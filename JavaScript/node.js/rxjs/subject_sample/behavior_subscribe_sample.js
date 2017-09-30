
const Rx = require('rxjs')

const src = Rx.Observable.range(1, 5)
const subject = new Rx.BehaviorSubject(0)

subject.subscribe(console.log)

src.subscribe(subject)

// NG
//subject.subscribe(console.log)
