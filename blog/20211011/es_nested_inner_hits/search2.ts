
import { send } from './es_util.ts'

const index = 'items'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
  _source: {
    excludes: ['editions']
  },
  query: {
    nested: {
      path: 'editions',
      query: {
        nested: {
          path: 'editions.sales',
          query: {
            bool: {
              must: [
                {
                  range: {
                    'editions.sales.date_from': { gte: '2021-07-01T00:00:00Z' }
                  }
                },
                {
                  range: {
                    'editions.sales.sale_price': { lte: 1500 }
                  }
                }
              ]
            }
          },
          inner_hits: {}
        }
      },
      inner_hits: {
        _source: {
          excludes: ['editions.sales']
        }
      }
    }
  }
})

console.log(JSON.stringify(body, null, 2))

console.log('-----')

const toDoc = (res: any) => res.hits.hits.map((r: any) => {
  const doc = { ...r._source }

  for (const [k, v] of Object.entries(r.inner_hits ?? {})) {
    const key = k.split('.').slice(-1)[0]
    doc[key] = toDoc(v)
  }

  return doc
})

const res = toDoc(body)

console.log(JSON.stringify(res, null, 2))
