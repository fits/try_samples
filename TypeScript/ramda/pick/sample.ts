import * as R from 'ramda'

const obj = {
    item: {
        id: {
            name: 'item A',
            value: 'item-1'
        },
        details: {
            color: 'white',
            size: 1
        },
        price: 110,
        priceWithoutTax: 100,
        tax: 10
    },
    qty: 3
}

const getQty = R.pick(['qty'])

const getPrice = R.pipe(
    R.pathOr({}, ['item']),
    R.pick(['priceWithoutTax', 'tax'])
)

const getId = R.pipe(
    R.path(['item', 'id', 'value']),
    R.objOf('id')
)

const getName = R.pipe(
    R.pathOr({}, ['item', 'id']),
    R.pick(['name'])
)

const convertTo = R.pipe(
    x => [x],
    R.ap([getId, getName, getPrice, getQty]),
    R.mergeAll
)

// { id: 'item-1', name: 'item A', priceWithoutTax: 100, tax: 10, qty: 3 }
console.log(convertTo(obj))
