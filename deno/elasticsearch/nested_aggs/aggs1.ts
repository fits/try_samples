
import { send } from './es_util.ts'

const index = 'items'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
  size: 0,
  aggs: {
    group_by_id: {
      terms: { field: 'id' },
      aggs: {
        editions: {
          nested: { path: 'editions' },
          aggs: {
            price_min: {
              min: { field: 'editions.price' }
            },
            price_max: {
              max: { field: 'editions.price' }
            }
          }
        }
      }
    }
  }
})

console.log(body)

console.log('-----')

console.log(body.aggregations.group_by_id.buckets)
