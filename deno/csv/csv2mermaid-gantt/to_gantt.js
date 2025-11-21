import { parse } from 'jsr:@std/csv'
import { format } from 'jsr:@std/datetime'
import * as R from 'npm:ramda'

const DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss.SSS'

const csv = await Deno.readTextFile(Deno.args[0])

const data = parse(csv, { skipFirstRow: true })

const toActionPeriod = R.pipe(
    R.map(R.props(['action', 'date'])),
    R.fromPairs,
)

const groupByMethod = R.pipe(
    R.groupBy(R.prop('method')),
    R.map(toActionPeriod),
)

const groupByClass = R.pipe(
    R.groupBy(R.prop('class')),
    R.map(groupByMethod),
)

const formatDate = (dv) => R.pipe(
    R.defaultTo(dv),
    v => format(new Date(v), DATE_FORMAT)
)

const renderTask = (period) => (v, k) => {
    const start = formatDate(period.start)(v.start)
    const end = formatDate(period.end)(v.end)

    console.log(`${k} : ${start},${end}`)
}

const renderSection = (period) => (v, k) => {
    console.log(`section ${k}`)
    R.forEachObjIndexed(renderTask(period), v)
}

const toPeriod = (v) => {
    const ds = R.map(R.prop('date'), v)

    const end = R.reduce(R.max, '', ds)
    const start = R.reduce(R.min, end, ds)

    return { start, end }
}

const renderGantt = (v) => {
    const period = toPeriod(v)

    R.forEachObjIndexed(renderSection(period), groupByClass(v))
}

renderGantt(data)
