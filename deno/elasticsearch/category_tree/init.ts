
import { send } from './es_util.ts'

const index = 'category_samples'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body: b1 } = await send(indexUrl, 'PUT', {
  mappings: {
    properties: {
      categories: {
        type: 'nested',
        properties: {
          code: { type: 'keyword' },
          sub_categories: {
            type: 'nested',
            properties: {
              code: { type: 'keyword' }
            }
          }
        }
      }
    }
  }
})

console.log(b1)

const items = [
  {
    id: 'id-001',
    name: 'item-1',
    categories: [
      {
        code: 'A', 
        name: 'A-Category', 
        sub_categories: [
          { code: 'A1', name: 'A1-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-002',
    name: 'item-2',
    categories: [
      {
        code: 'A', 
        name: 'A-Category', 
        sub_categories: [
          { code: 'A3', name: 'A3-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-003',
    name: 'item-3',
    categories: [
      {
        code: 'B', 
        name: 'B-Category', 
        sub_categories: [
          { code: 'B2', name: 'B2-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-004',
    name: 'item-4',
    categories: [
      {
        code: 'C', 
        name: 'C-Category', 
        sub_categories: [
          { code: 'C7', name: 'C7-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-005',
    name: 'item-5',
    categories: [
      {
        code: 'C', 
        name: 'C-Category', 
        sub_categories: [
          { code: 'C9', name: 'C9-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-006',
    name: 'item-6',
    categories: [
      {
        code: 'A', 
        name: 'A-Category', 
        sub_categories: [
          { code: 'A3', name: 'A3-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-007',
    name: 'item-7',
    categories: [
      {
        code: 'C', 
        name: 'C-Category', 
        sub_categories: [
          { code: 'C9', name: 'C9-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-008',
    name: 'item-8',
    categories: [
      {
        code: 'A', 
        name: 'A-Category', 
        sub_categories: [
          { code: 'A1', name: 'A1-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-009',
    name: 'item-9',
    categories: [
      {
        code: 'A', 
        name: 'A-Category', 
        sub_categories: [
          { code: 'A1', name: 'A1-SubCategory' }
        ]
      }
    ]
  },
  {
    id: 'id-010',
    name: 'item-10',
    categories: [
      {
        code: 'B', 
        name: 'B-Category', 
        sub_categories: [
          { code: 'B4', name: 'B4-SubCategory' }
        ]
      }
    ]
  },
]

for (const it of items) {
  const { body: b2 } = await send(`${indexUrl}/_doc/${it.id}`, 'PUT', it)
  console.log(b2)
}
