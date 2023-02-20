import { readAll } from "https://deno.land/std@0.177.0/streams/read_all.ts"

const file = await Deno.open(Deno.args[0])

const buf = await readAll(file)

file.close()

console.log(buf)
