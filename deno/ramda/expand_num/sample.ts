import * as R from 'https://deno.land/x/ramda/mod.ts'

type GetNum<T> = (data: T) => number

function expand_num<T>(numProp: GetNum<T>, ds: T[]): T[] {
    return ds.flatMap((x, i) => 
        [...Array(numProp(x)).keys()].map(j => Object.assign({ index: [i, j] }, x))
    )
}

const mapIndexed = R.addIndex(R.map)

function expand_num1<T>(numProp: string) {
    return R.pipe(
        mapIndexed((x: T, i: number) => 
            R.times(
                (j: number) => R.mergeLeft(x, { index: [i, j] }), 
                R.prop(numProp, x)
            )
        ),
        R.flatten,
    )
}

const d1 = [
    { id: 'a1', qty: 2 },
    { id: 'b2', qty: 1 },
    { id: 'c3', qty: 3 },
    { id: 'd4', qty: 0 },
]

console.log( expand_num(x => x.qty, d1) )

console.log( expand_num1('qty')(d1) )
