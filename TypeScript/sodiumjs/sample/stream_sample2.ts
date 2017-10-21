
interface Message {
	type: string
	body: string
}

import { StreamSink } from 'sodiumjs'

const src = new StreamSink<Message>()

const category = src.filter(d => d.type == 'category').map(m => m.body)
category.listen(c => console.log(`category = ${c}`))

const value = src.filter(d => d.type == 'value').map(m => m.body)
value.listen(v => console.log(`value = ${v}`))

value.snapshot(category.hold(''), (v, c) => [c, v])
	.listen(d => console.log(`*** ${d}`))

src.send({type: 'category', body: 'type1'})

src.send({type: 'value', body: '1'})

src.send({type: 'category', body: 'type2'})

src.send({type: 'value', body: '2'})

src.send({type: 'invalid', body: 'test'})

src.send({type: 'value', body: '3'})
