import { readAll } from "https://deno.land/std@0.186.0/streams/read_all.ts"

const file = await Deno.open(Deno.args[0])
const param = parseInt(Deno.args[1] ?? '10')
const func = Deno.args[2] ?? 'sample'

const buf = await readAll(file)

file.close()

const instance = (await WebAssembly.instantiate(buf, {})).instance

console.log(instance.exports[func](param))
