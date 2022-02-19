import * as fs from 'fs'
import * as readline from 'readline'

const file = process.argv[2]
const query = process.argv[3]

const parseQuery = (query) => 
    query.split(',').map(q => { 
        const [f, s] = q.split(':').map(s => s.trim())
        return { field: f, query: s.toLowerCase()}
    })

const loadLogs = async (file) => {
    const rs = []

    const reader = readline.createInterface({
        input: fs.createReadStream(file)
    })

    for await (const line of reader) {
        rs.push(JSON.parse(line))
    }

    return rs
} 

const search = (docs, filters) => 
    docs.filter(d => 
        filters.reduce((acc, f) => {
            if (acc) {
                const v = d[f.field]?.toString().toLowerCase()
                acc = v?.includes(f.query) ?? false
            }
            return acc
        }, true)
    )

const run = async () => {
    const filters = parseQuery(query)

    const t1 = Date.now()

    const docs = await loadLogs(file)

    const t2 = Date.now()
    
    const rs = search(docs, filters)

    const t3 = Date.now()

    console.log(`logs size = ${docs.length}`)
    console.log(`load time = ${t2 - t1}`)

    console.log(`search logs size = ${rs.length}`)
    console.log(`search time = ${t3 - t2}`)
}

run().catch(err => console.error(err))
