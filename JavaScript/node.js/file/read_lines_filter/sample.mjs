import * as fs from 'fs'
import * as readline from 'readline'

const file = process.argv[2]
const term = process.argv[3]

const readLines = async (file) => {
    const reader = readline.createInterface({
        input: fs.createReadStream(file)
    })

    const res = []

    for await (const line of reader) {
        res.push(line)
    }

    return res
}

const filter = (ds, term) => ds.filter(d => d.includes(term))

const run = async () => {
    const t1 = Date.now()

    const ds = await readLines(file)

    const t2 = Date.now()

    const rs = filter(ds, term)

    const t3 = Date.now()

    console.log(`total = ${ds.length}, filtered = ${rs.length}`)

    console.log(`read time = ${t2 - t1} ms`)
    console.log(`filter time = ${t3 - t2} ms`)
}

run().catch(err => console.error(err))
