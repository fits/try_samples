import * as R from 'https://deno.land/x/ramda/mod.ts'

const data = ['A', 'S', 'R', 'E', 'C']

const powerSet = R.reduce(
    (acc, v) => R.concat(acc, R.map(R.append(v), acc)),
    [[]],
)

const powerSet2 = R.reduce(
    (acc, v) => R.converge(R.concat, [R.identity, R.map(R.append(v))])(acc),
    [[]],
)

const powerSet3 = R.reduce(
    (acc, v) => R.chain(R.concat, R.map(R.append(v)))(acc),
    [[]],
)

const comb = f => R.pipe(
    R.sort(R.ascend(R.identity)),
    f,
    R.filter(x => x.length > 0),
)

const ps = [
    comb(powerSet),
    comb(powerSet2),
    comb(powerSet3),
]

ps.forEach(f => {
    console.log(f(data))
    console.log('-----')
})
