import { readAll } from "https://deno.land/std@0.177.0/streams/read_all.ts"

const file = await Deno.open(Deno.args[0])

const buf = await readAll(file)

file.close()

const module = await WebAssembly.compile(buf)

const imports = WebAssembly.Module.imports(module)
const exports = WebAssembly.Module.exports(module)

console.log('--- imports ---')
console.log(imports)

console.log('--- exports ---')
console.log(exports)
