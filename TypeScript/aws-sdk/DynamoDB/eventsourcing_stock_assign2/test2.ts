
import { StockAction } from './stock'

const id = new Date().toISOString()

const N = 50

const INITIAL_QTY = 10
const MAX_ASSIGN_QTY = 3

const run = async () => {
    await StockAction.arrive(id, INITIAL_QTY)

    const start = Date.now()

    const rs = []

    for (let i = 0; i < N; i++) {
        const aqty = Math.floor(Math.random() * (MAX_ASSIGN_QTY - 1)) + 1

        rs.push(
            StockAction.assign(id, aqty)
                .catch(err => console.error(err.message))
        )
    }

    for (const r of rs) {
        console.log(await r)
    }

    console.log(`*** time: ${ (Date.now() - start) / 1000 }ms`)

    console.log(await StockAction.find(id))
    console.log(await StockAction.restore(id))
}

run().catch(err => console.error(err))