
import { parse } from './parallel_gc_parser.ts'

const data = await Deno.readTextFile(Deno.args[0])

const res = parse(data.split('\n'))

console.log(JSON.stringify(res, null, 2))
