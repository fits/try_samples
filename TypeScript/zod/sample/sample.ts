import { z } from 'zod'

const Item = z.object({
    id: z.string(),
    name: z.optional(z.string()),
    price: z.number(),
})

const r1 = Item.parse({ id: 'item-1', price: 100 })
console.log(r1)

try {
    Item.parse({ id: 'item-0' })
} catch(e) {
    console.error(e)
}

const r2 = Item.parse({ id: 'item-2', name: 'ITEM 2', price: 200 })
console.log(r2)
