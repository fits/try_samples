
import { Evt } from 'https://deno.land/x/evt/mod.ts'

const evt1 = new Evt<string>()

evt1.attach(txt => console.log(txt))

evt1.post('one')
evt1.post('two')

type Event = {'created': string} | {'updated': number}

const evt2 = new Evt<Event>()

evt2.attach(ev => console.log(ev))

evt2.post({'created': 'event1'})
evt2.post({'created': 'event2'})
evt2.post({'updated': 1})
evt2.post({'updated': 2})
