
import ky from 'https://deno.land/x/ky/index.js'

const api = ky.create({prefixUrl: 'http://localhost:3000'})

const item1 = 'item-1'
const loc1 = 'virtual-1'
const loc2 = 'warehouse-1'
const loc3 = 'customer-1'

const registStocks = async () => {
    const list = [ [loc1, false], [loc2, true], [loc3, false] ]
    
    list.forEach( ([loc, managed]) => 
        api.put('stocks', {
            json: {
                item: item1,
                location: loc,
                managed: managed
            }
        })
        .json()
        .then(r => console.log(`*** created stock: ${JSON.stringify(r)}`))
        .catch(err => console.error(`*** failed create: ${item1}, ${loc}`))
    )
}

const arrive = async () => {
    const r1 = await api.post('moves', {
        json: {
            item: item1,
            qty: 10,
            from: loc1,
            to: loc2
        }
    }).json()

    console.log(r1)

    const r2 = await api.put(`moves/${r1.Draft.id}/arrival`, {
        json: { qty: 10 }
    }).json()

    console.log(r2)
}

const arriveAfterAssign = async () => {
    const r1 = await api.post('moves', {
        json: {
            item: item1,
            qty: 2,
            from: loc2,
            to: loc3
        }
    }).json()

    console.log(r1)

    const id = r1.Draft.id

    const r2 = await api.put(`moves/${id}/assign`).json()

    console.log(r2)
    await printStock(item1, loc2)

    const r3 = await api.put(`moves/${id}/shipment`, {
        json: { qty: 2 }
    }).json()

    console.log(r3)
    await printStock(item1, loc2)

    const r4 = await api.put(`moves/${id}/arrival`, {
        json: { qty: 2 }
    }).json()

    console.log(r4)
}

const printStock = (item, location) => {
    api.get(`stocks/${item}/${location}`)
        .json()
        .then(s => console.log(s))
        .catch(err => console.error(`failed get stock: ${item}, ${location}`))
}

const run = async () => {
    await registStocks()
    await arrive()
    await arriveAfterAssign()
}

run().catch(err => console.error(err))
