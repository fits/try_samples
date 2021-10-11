
import { send } from './es_util.ts'

const index = 'items'
const baseUrl = 'http://localhost:9200'

const indexUrl = `${baseUrl}/${index}`

const { body: b1 } = await send(indexUrl, 'PUT', {
  mappings: {
    properties: {
      id: { type: 'keyword' },
      name: { type: 'text' },
      editions: {
        type: 'nested',
        properties: {
          edition: { type: 'keyword' },
          price: {
            type: 'scaled_float',
            scaling_factor: 100
          },
          sales: {
            type: 'nested',
            properties: {
              sale_price: {
                type: 'scaled_float',
                scaling_factor: 100
              },
              date_from: { type: 'date' },
              date_to: { type: 'date' }
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
    name: 'item-A',
    editions: [
      {
        edition: 'Standard',
        price: 1000,
        sales: [
          {
            sale_price: 900,
            date_from: '2021-01-01T00:00:00Z',
            date_to: '2021-01-03T00:00:00Z'
          },
          {
            sale_price: 800,
            date_from: '2021-07-01T00:00:00Z',
            date_to: '2021-07-05T00:00:00Z'
          }
        ]
      },
      {
        edition: 'Extra',
        price: 2000,
        sales: [
          {
            sale_price: 1800,
            date_from: '2021-01-01T00:00:00Z',
            date_to: '2021-01-03T00:00:00Z'
          },
          {
            sale_price: 1700,
            date_from: '2021-07-01T00:00:00Z',
            date_to: '2021-07-05T00:00:00Z'
          },
          {
            sale_price: 1500,
            date_from: '2021-09-01T00:00:00Z',
            date_to: '2021-09-02T00:00:00Z'
          }
        ]
      }
    ]
  },
  {
    id: 'id-002',
    name: 'item-B',
    editions: [
      {
        edition: 'Standard',
        price: 1500,
        sales: [
          {
            sale_price: 1400,
            date_from: '2021-09-01T00:00:00Z',
            date_to: '2021-09-05T00:00:00Z'
          }
        ]
      },
      {
        edition: 'Extra',
        price: 5000,
        sales: []
      }
    ]
  },
  {
    id: 'id-003',
    name: 'item-C',
    editions: [
      {
        edition: 'Standard',
        price: 7000,
        sales: [
          {
            sale_price: 6800,
            date_from: '2021-01-01T00:00:00Z',
            date_to: '2021-01-03T00:00:00Z'
          },
          {
            sale_price: 6700,
            date_from: '2021-02-01T00:00:00Z',
            date_to: '2021-02-05T00:00:00Z'
          },
          {
            sale_price: 6600,
            date_from: '2021-04-01T00:00:00Z',
            date_to: '2021-04-02T00:00:00Z'
          },
          {
            sale_price: 6500,
            date_from: '2021-07-01T00:00:00Z',
            date_to: '2021-07-15T00:00:00Z'
          }
        ]
      }
    ]
  },
  {
    id: 'id-004',
    name: 'item-D',
    editions: [
      {
        edition: 'Standard',
        price: 4000,
        sales: []
      },
      {
        edition: 'Extra',
        price: 6000,
        sales: []
      },
      {
        edition: 'Premium',
        price: 9000,
        sales: []
      }
    ]
  }
]

for (const it of items) {
  const { body: b2 } = await send(`${indexUrl}/_doc/${it.id}`, 'PUT', it)
  console.log(b2)
}
