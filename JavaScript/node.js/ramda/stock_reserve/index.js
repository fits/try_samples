
const R = require('ramda')

const data = [
    {location: 'loc1', qty: 5, reserved: ['a1', 'b2', 'a1']},
    {location: 'loc2', qty: 10, reserved: ['c3', 'a1']}
]

const reservedLens = R.lensProp('reserved')

const reservedQty = R.pipe(R.propOr([], 'reserved'), R.length)

const appendReservation = (key, qty) => 
    R.over(reservedLens, R.flip(R.concat)(R.repeat(key, qty)))

const clearReservation = key =>
    R.map(
        R.over(reservedLens, R.reject(R.equals(key)))
    )

const reserveAny = (key, qty) =>
    R.reduce(
        (acc, x) => {
            if (acc.qty > 0) {
                const n = R.max(0, R.min(acc.qty, x.qty - reservedQty(x)))

                x = appendReservation(key, n)(x)

                acc.qty -= n
            }

            acc.result.push(x)
            return acc
        },
        {qty: qty, result: []}
    )

const reserveAll = (key, qty) => 
    R.pipe(
        reserveAny(key, qty),
        R.ifElse(
            R.propSatisfies(R.equals(0), 'qty'),
            R.prop('result'),
            R.pipe(R.prop('result'), R.empty)
        )
    )

const rebook = (key, qty) =>
    R.pipe(
        clearReservation(key),
        reserveAll(key, qty)
    )


console.log( clearReservation('a1')(data) )

console.log('-------------------------------')

console.log( reserveAny('d2', 20)(data).result )

console.log('-------------------------------')


console.log( reserveAll('d2', 20)(data) )

console.log( reserveAll('d2', 10)(data) )

console.log( reserveAll('d2', -1)(data) )

console.log( reserveAll('d2', 1)(data) )

console.log('-------------------------------')

console.log( rebook('a1', 2)(data) )

console.log( rebook('a1', 10)(data) )

console.log( rebook('a1', 14)(data) )

console.log( rebook('a1', 0)(data) )
