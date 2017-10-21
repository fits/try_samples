
import { StreamSink } from 'sodiumjs'

const s1 = new StreamSink<string>()
const s2 = new StreamSink<string>()

const d1 = s1.orElse(s2).hold('init1')

d1.listen(v => console.log(`*** d1 = ${v}`))

s1.send('s1-1')

s2.send('s2-1')

s1.send('s1-2')

s1.send('s1-3')

s2.send('s2-2')
