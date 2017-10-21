
const Rx = require('rxjs/Rx')

const category = new Rx.Subject()
const value = new Rx.Subject()

category.subscribe(c => console.log(`category = ${c}`))
value.subscribe(v => console.log(`value = ${v}`))

const src = new Rx.Subject()

src.filter(d => d.type == 'category')
	.map(d => d.data)
	.subscribe(category)

src.filter(d => d.type == 'value')
	.map(d => d.data)
	.subscribe(value)

Rx.Observable.combineLatest(category, value)
	.subscribe(v => console.log(`*** combine = ${v}`))

src.next({type: 'category', data: 'type1'})

src.next({type: 'value', data: '1'})

src.next({type: 'category', data: 'type2'})

src.next({type: 'value', data: '2'})

src.next({type: 'invalid', data: 'test'})

src.next({type: 'value', data: '3'})
