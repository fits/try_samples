
const moduleType = process.argv[2] ?? 'redis1'

const n = parseInt(process.argv[3] ?? '100')
const stockQty = parseInt(process.argv[4] ?? '150')
const maxAssignQty = parseInt(process.argv[5] ?? '3')

const ID = 'stock-1'

const sleep = t => new Promise(resolve => {
    setTimeout(resolve, t)
})

const run = async () => {
    const { init, assign } = await import(`./${moduleType}`)

    await init(ID, stockQty)

    await assign(ID, 'dummy-assign', 1)

    sleep(1000)

    const s = Date.now()

    for (let i = 1; i <= n; i++) {
        const q = Math.round(Math.random() * (maxAssignQty - 1)) + 1

        try {
            await assign(ID, `assign-${i}`, q)
        } catch(e) {
            console.log(`ERROR: ${i} - ${e.message}`)
        }
    }

    console.log(`time: ${Date.now() - s} ms`)
}

run()
    .catch(err => console.error(err))
    .finally(() => {
        process.exit(0)
    })
