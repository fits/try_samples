import { z } from 'zod'

const Data = z.object({
    id: z.string(),
    date: z.string().datetime({ offset: true }),
    attrs: z.record(z.string())
})

const r1 = Data.parse({ id: 'data-1', date: '2023-04-13T01:00:00+09:00', attrs: { category: 'test', year: '2023' } })
console.log(r1)

try {
    Data.parse({ id: 'data-1', date: '2023-04-13T01:00:00+09:00', attrs: { value: 1 } })
} catch(e) {
    console.log(JSON.stringify(e))
}