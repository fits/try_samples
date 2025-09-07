import { createLocalJWKSet, jwtVerify } from 'npm:jose'

const jwk = JSON.parse(await Deno.readTextFile(Deno.args[0]))
const token = Deno.args[1]

const jwks = createLocalJWKSet({ keys: [ jwk ]})

const { payload, protectedHeader } = await jwtVerify(token, jwks, {})

console.log(protectedHeader)
console.log(payload)
