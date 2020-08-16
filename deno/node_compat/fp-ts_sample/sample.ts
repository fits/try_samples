
import { createRequire } from 'https://deno.land/std/node/module.ts'

const require = createRequire(import.meta.url)

const { some, none } = require('fp-ts/lib/Option')

console.log( some(12) )
console.log( none )

const { option } = require('fp-ts/lib/index')

console.log( some(12) )
console.log( none )
