import { send } from './es_util.ts'

const index = 'sample'
const baseUrl = 'http://localhost:9200'
const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
  size: 0,
  aggs: {
    categories: {
      nested: { path: 'categories' },
      aggs: {
        count_items: {
          terms: {
            field: 'categories.code',
            order: { _term: 'asc' }
          }
        }
      }
    },
    children: {
      nested: { path: 'categories.children' },
      aggs: {
        count_items: {
          terms: {
            field: 'categories.children.code',
            order: { _term: 'asc' }
          }
        }
      }
    }
  }
})

console.log(JSON.stringify(body, null, 2))
