import { parse } from 'jsr:@std/csv'
import { format } from 'jsr:@std/datetime'
import * as R from 'npm:ramda'

const DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss.SSS'

const csv = await Deno.readTextFile(Deno.args[0])

const data = parse(csv, { skipFirstRow: true })

const groupByMethod = R.pipe(
    R.groupBy(R.prop('method')),
    R.map(
        R.pipe(
            R.project(['action', 'date']),
            R.sortBy(R.prop('date')),
        )
    ),
)

const groupByClass = R.pipe(
    R.groupBy(R.prop('class')),
    R.map(groupByMethod),
)

const formatDate = (v) => format(new Date(v), DATE_FORMAT)

const printTask = (name, start, end) => {
    console.log(`${name} : ${formatDate(start)},${formatDate(end)}`)
}

const renderTask = (period) => (v, k) => {
    const [remain, _] = R.reduce(
        ([buf, offset], x) => {
            if (R.propEq('start', 'action', x)) {
                buf.push(x)
            }
            else if (R.propEq('end', 'action', x)) {
                printTask(k, buf.shift()?.date ?? offset, x.date)
                offset = x.date
            }
            return [buf, offset]
        },
        [[], period.start], 
        v
    )

    R.reduce(
        (offset, x) => {
            printTask(k, x.date, offset)
            return x.date
        },
        period.end,
        remain.reverse()
    )
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
