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
          },
          aggs: {
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
        }
      }
    }
  }
})

console.log(JSON.stringify(body, null, 2))

console.log('----------')

const fieldName = (rs: any) => 
  Object.keys(rs).find(k => !['key', 'doc_count'].includes(k))

type Bucket = { key: string, doc_count: number }

const toDoc = (rs: any) => {
  const k1 = fieldName(rs)

  if (!k1) {
    return {}
  }

  const k2 = fieldName(rs[k1])!

  const bs = rs[k1][k2].buckets.map((b: Bucket) => 
    Object.assign(
      {
        code: b.key,
        [k2]: b.doc_count
      },
      toDoc(b)
    )
  )

  return {
    [k1]: bs
  }
}

const res = toDoc(body.aggregations)

console.log(JSON.stringify(res, null, 2))
