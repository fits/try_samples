
import { Monad } from 'funland'
import { Option, OptionModule } from 'funfix'

const m: Monad<'funfix/option'> = OptionModule

const f = o => m.map(v => `value:${v}`, o) as Option<string>

const d1 = m.of(10)

console.log( d1 )
console.log( f(d1) )

console.log( f(Option.none()) )

console.log( f(d1).getOrElse('none') )
console.log( f(Option.none()).getOrElse('none') )
