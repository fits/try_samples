import { readLines } from 'https://deno.land/std/io/mod.ts'

for await (const line of readLines(Deno.stdin)) {
    const d = line.trim()

    console.log(d)
}
