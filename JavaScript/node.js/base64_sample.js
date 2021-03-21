
const {Buffer} = require('buffer')

const d = {
	name: 'a',
	value: 1
}

const enc = Buffer.from(JSON.stringify(d)).toString('base64')

console.log(enc)

const dec = Buffer.from(enc, 'base64').toString()

console.log(dec)
