import * as fs from 'fs'
import * as readline from 'readline'

const file = process.argv[2]

const run = async () => {
    const reader = readline.createInterface({
        input: fs.createReadStream(file)
    })

    for await (const line of reader) {
        console.log(line)
    }
}

run().catch(err => console.error(err))
