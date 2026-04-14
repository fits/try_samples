import * as R from 'https://deno.land/x/ramda/mod.ts'
import { TextLineStream } from 'jsr:@std/streams/text-line-stream'

const powerSet = R.reduce(
    (acc, v) => R.chain(R.concat, R.map(R.append(v)))(acc),
    [[]],
)

const comb = R.pipe(
    R.sort(R.ascend(R.identity)),
    powerSet,
    R.filter(x => x.length > 0),
)

const parseCsv = R.pipe(
    R.split(','),
    R.map(R.trim),
)

const topN = n => R.pipe(
    R.countBy(R.join(',')),
    R.toPairs,
    R.sort(R.descend(R.nth(1))),
    R.take(n)
)

const stream = Deno.stdin.readable
    .pipeThrough(new TextDecoderStream())
    .pipeThrough(new TextLineStream())

let data = []

for await (const line of stream) {
    data = R.concat(data, comb(parseCsv(line)))
}

console.log(topN(5)(data))

console.log('-----')

console.log(topN(10)(data))
