import express from 'express'
import { z } from 'zod'

const port = parseInt(process.env.PORT ?? '3000')

const Item = z.object({
    item: z.string(),
    amount: z.number()
})

const FindItem = z.object({
    item: z.string()
})

type Item = z.infer<typeof Item>

const store: { [key: string]: Item } = {}

const app = express()

app.use(express.json())

app.post('/add', (req, res) => {
    try {
        const input = Item.parse(req.body)

        if (input.item in store) {
            res.json({ error: 'exists' })
            return
        }
    
        store[input.item] = input
        res.json(input)

    } catch(e) {
        res.json({ error: e })
    }
})

app.post('/get', (req, res) => {
    try {
        const input = FindItem.parse(req.body)

        if (!(input.item in store)) {
            res.json({ error: 'notfound' })
            return
        }

        res.json(store[input.item])

    } catch(e) {
        res.json({ error: e })
    }
})

app.listen(port, () => {
    console.log(`started: port=${port}`)
})
