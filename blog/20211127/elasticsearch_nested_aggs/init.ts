import { send } from './es_util.ts'

const index = 'sample'
const baseUrl = 'http://localhost:9200'
const indexUrl = `${baseUrl}/${index}`

const N = 20
const cts1 = ['A', 'B', 'C']
const cts2 = ['S', 'T', 'U']

const { status } = await fetch(indexUrl, { method: 'HEAD' })

if (status == 200) {
  await fetch(indexUrl, { method: 'DELETE' })
}

const { body } = await send(indexUrl, 'PUT', {
  mappings: {
    dynamic_templates: [
      {
        string_keyword: {
          match_mapping_type: 'string',
          mapping: {
            type: 'keyword'
          }
        }
      }
    ],
    properties: {
      categories: {
        type: 'nested',
        properties: {
          children: {
            type: 'nested'
          }
        }
      }
    }
  }
})

console.log(body)

const selectCategory = (cts: Array<string>) => 
  cts[Math.round(Math.random() * (cts.length - 1))]

for (let i = 0; i < N; i++) {
  const ct1 = selectCategory(cts1)
  const ct2 = selectCategory(cts2)

  const { body: r } = await send(`${indexUrl}/_doc`, 'POST', {
    name: `item-${i + 1}`,
    categories: [
      {
        code: ct1,
        name: `category${ct1}`,
        children: [
          {
            code: ct2,
            name: `subcategory${ct1}${ct2}`
          }
        ]
      }
    ]
  })

  console.log(`result: ${r.result}, _id: ${r._id}`)
}
