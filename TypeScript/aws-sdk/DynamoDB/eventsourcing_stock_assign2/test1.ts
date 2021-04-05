
import { StockAction } from './stock'

const dumpStock = async (stockid) => {
    const st = await StockAction.find(stockid)
    const st2 = await StockAction.restore(stockid)

    console.log(`*** stock: ${JSON.stringify(st)}, ${JSON.stringify(st2)}`)
}

const run = async () => {
    const id = new Date().toISOString()

    await dumpStock(id)

    const ev1 = await StockAction.arrive(id, 5)
    console.log(ev1)

    await dumpStock(id)

    const ev2 = await StockAction.assign(id, 2)
    console.log(ev2)

    await dumpStock(id)

    try {
        await StockAction.assign(id, 4)
    } catch(e) {
        console.log(`*** ERROR: ${e.message}`)
    }

    const ev3 = await StockAction.arrive(id, 1)
    console.log(ev3)

    await dumpStock(id)

    const ev4 = await StockAction.assign(id, 4)
    console.log(ev4)

    await dumpStock(id)
}

run().catch(err => console.error(err))