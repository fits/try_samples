import { readAll } from "https://deno.land/std@0.197.0/streams/read_all.ts"

const file = await Deno.open(Deno.args[0])

const func = Deno.args[1] ?? 'calc'
const param1 = parseInt(Deno.args[2] ?? '10')
const param2 = parseInt(Deno.args[3] ?? '20')

const buf = await readAll(file)

file.close()

const instance = (await WebAssembly.instantiate(buf, {})).instance

const res = instance.exports[func](param1, param2)

console.log(res)
