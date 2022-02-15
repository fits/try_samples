import flexsearch from 'flexsearch'
const { Document } = flexsearch

const index = new Document({
    index: ['name', 'color', 'price']
})

const initData = async () => {
    await index.addAsync({ id: 1, name: 'item-1', color: ['White'], price: 100 })
    await index.addAsync({ id: 2, name: 'test2-white', color: ['Black', 'Red'], price: 2000 })
    await index.addAsync({ id: 3, name: 'sample-3_test-B', color: ['Blue', 'White'], price: 30 })
    await index.addAsync({ id: 4, name: 'sample 4-1', color: ['Green', 'Blue'], price: 4000 })
    await index.addAsync({ id: 5, name: 'test item 5', color: ['Black', 'Red', 'White'], price: 500 })
}

const run = async () => {
    await initData()

    const r1 = await index.searchAsync('item', ['name'])
    console.log(r1)

    const r2 = await index.searchAsync('white', ['color'])
    console.log(r2)

    const r3 = await index.searchAsync('white', ['name', 'color'])
    console.log(r3)

    console.log('-----')

    console.log(index.index.name.map)
    console.log(index.index.color.map)
    console.log(index.index.price.map)
}

run().catch(err => console.error(err))
