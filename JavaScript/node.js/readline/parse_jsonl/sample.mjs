import fs from 'fs'
import readline from 'readline'

const reader = readline.createInterface({
    input: fs.createReadStream(process.argv[2])
})

const lines = await Array.fromAsync(reader)

const items = lines.map(JSON.parse)

const res = items
    .filter(x => x.variants?.some(v => v.color == 'white'))
    .map(({id, name}) => ({ id, name }))

console.log(res)
