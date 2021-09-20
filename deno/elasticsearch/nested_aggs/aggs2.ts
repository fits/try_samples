
import { send } from './es_util.ts'

const index = 'items'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body: b1 } = await send(`${indexUrl}/_search`, 'POST', {
  size: 0,
  aggs: {
    group_by_id: {
      terms: { field: 'id' },
      aggs: {
        editions: {
          nested: { path: 'editions' },
          aggs: {
            sales: {
              nested: { path: 'editions.sales' },
              aggs: {
                sale_price_min: {
                  min: { field: 'editions.sales.sale_price' }
                },
                date_to_last: {
                  max: { field: 'editions.sales.date_to' }
                }
              }
            }
          }
        }
      }
    }
  }
})

console.log(b1)

console.log('-----')

console.log(JSON.stringify(b1.aggregations.group_by_id.buckets))

console.log('-----')

const { body: b2 } = await send(`${indexUrl}/_search`, 'POST', {
  size: 0,
  aggs: {
    group_by_id: {
      terms: { field: 'id' },
      aggs: {
        editions: {
          nested: { path: 'editions' },
          aggs: {
            edition: {
              terms: { field: 'editions.edition' },
              aggs: {
                sales: {
                  nested: { path: 'editions.sales' },
                  aggs: {
                    sale_price_min: {
                      min: { field: 'editions.sales.sale_price' }
                    },
                    date_to_last: {
                      max: { field: 'editions.sales.date_to' }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
})

console.log(JSON.stringify(b2.aggregations.group_by_id.buckets, null, 2))
