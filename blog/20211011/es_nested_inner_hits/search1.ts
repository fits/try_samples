
import { send } from './es_util.ts'

const index = 'items'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
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
          }
        }
      }
    }
  }
})

console.log(JSON.stringify(body, null, 2))
