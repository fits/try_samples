
import { Option, OptionModule } from 'funfix'

const f = o => OptionModule.map(v => `value:${v}`, o) as Option<string>
//const f = (o: Option<number>) => OptionModule.map(v => `value:${v}`, o) as Option<string>

const d1 = OptionModule.of(10)
// default type HK<'funfix/option', number>
//const d1: Option<number> = OptionModule.of(10) as Option<number>

console.log( d1 )
console.log( f(d1) )

console.log( f(Option.none()) )

console.log( f(d1).getOrElse('none') )
console.log( f(Option.none()).getOrElse('none') )
