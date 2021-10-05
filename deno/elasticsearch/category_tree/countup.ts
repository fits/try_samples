
import { send } from './es_util.ts'

const index = 'category_samples'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
  size: 0,
  aggs: {
    categories: {
      nested: { path: 'categories' },
      aggs: {
        count: {
          multi_terms: {
            terms: [
              { field: 'categories.code' },
              { field: 'categories.name.keyword' }
            ],
            order: { _term: 'asc' }
          },
          aggs: {
            sub_categories: {
              nested: { path: 'categories.sub_categories' },
              aggs: {
                count: {
                  multi_terms: {
                    terms: [
                      { field: 'categories.sub_categories.code' },
                      { field: 'categories.sub_categories.name.keyword' }
                    ],
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

const print = (obj: any) => console.log(JSON.stringify(obj, null, 2))

print(body)

console.log('-----')

const isNotKeyword = (key: string) =>
  !['key', 'key_as_string', 'doc_count'].includes(key)

const fieldName = (rs: any) => {
  const ks = Object.keys(rs).filter(isNotKeyword)
  return ks.length > 0 ? ks[0] : undefined
}

const toDoc = (rs: any) => {
  const k1 = fieldName(rs)

  if (!k1) {
    return {}
  }

  const k2 = fieldName(rs[k1])!

  const bs = rs[k1][k2].buckets.map((b: any) => 
    Object.assign(
      {
        code: b.key[0],
        name: b.key[1],
        [k2]: b.doc_count
      },
      toDoc(b)
    )
  )

  return {
    [k1]: bs
  }
}

print(toDoc(body.aggregations))
