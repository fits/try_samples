import { v4, parse } from 'uuid'

const uuid = v4()

console.log(uuid)

const b = parse(uuid)

console.log(b.length)

const res = Buffer.from(b).toString('base64')

console.log(res)
