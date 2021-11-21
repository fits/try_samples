import { send } from './es_util.ts'

const index = Deno.env.get('ES_INDEX') ?? 'sample'
const baseUrl = 'http://localhost:9200'
const indexUrl = `${baseUrl}/${index}`

const { body } = await send(`${indexUrl}/_search`, 'POST', {
    size: 0,
    aggs: {
        categories: {
            nested: { path: 'categories' },
            aggs: {
                count_items: {
                    multi_terms: {
                        terms: [
                            { field: 'categories.code' },
                            { field: 'categories.name' }
                        ],
                        order: { _term: 'asc' }
                    },
                    aggs: {
                        children: {
                            nested: { path: 'categories.children' },
                            aggs: {
                                count_items: {
                                    multi_terms: {
                                        terms: [
                                            { field: 'categories.children.code' },
                                            { field: 'categories.children.name' }

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

console.log(JSON.stringify(body, null, 2))

console.log('----------')

const isNotKeyword = (key: string) =>
    !['key', 'key_as_string', 'doc_count'].includes(key)

const fieldName = (rs: any) => {
  const ks = Object.keys(rs).filter(isNotKeyword)
  return ks.length > 0 ? ks[0] : undefined
}

type Bucket = { key: Array<string>, doc_count: number }

const toDoc = (rs: any) => {
  const k1 = fieldName(rs)

  if (!k1) {
    return {}
  }

  const k2 = fieldName(rs[k1])!

  const bs = rs[k1][k2].buckets.map((b: Bucket) => 
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

const res = toDoc(body.aggregations)

console.log(JSON.stringify(res, null, 2))
