import { ServiceBroker, Context } from 'moleculer'

const broker = new ServiceBroker()

type CartId = string
type ItemId = string

interface Item {
    id: ItemId,
    price: number
}

interface FindItem {
    id: ItemId
}

interface CreateCart {
    id: CartId
}

interface AddItem {
    id: CartId,
    item: ItemId,
    qty: number
}

interface Cart {
    id: CartId,
    lines: Array<CartLine>
}

interface CartLine {
    item: Item,
    qty: number
}

type Option<T> = T | undefined | null

const itemStore = new Map<string, Item>()
const cartStore = new Map<string, Cart>()

const item = {
    name: 'item',
    actions: {
        async create(ctx: Context): Promise<void> {
            const params = ctx.params as Item
            itemStore.set(params.id, params)
        },
        async find(ctx: Context): Promise<Option<Item>> {
            const params = ctx.params as FindItem
            return itemStore.get(params.id)
        }
    }
}

const cart = {
    name: 'cart',
    actions: {
        async create(ctx: Context): Promise<Cart> {
            const params = ctx.params as CreateCart

            const cart: Cart = { id: params.id, lines: [] }
            cartStore.set(cart.id, cart)

            return cart
        },
        async addItem(ctx: Context): Promise<Option<Cart>> {
            const params = ctx.params as AddItem

            if (params.qty <= 0) {
                return undefined
            }

            const cart = cartStore.get(params.id)

            if (!cart) {
                return undefined
            }

            const item = await ctx.call('item.find', { id: params.item }) as Option<Item>

            if (!item) {
                return undefined
            }

            const newCart: Cart = {
                id: cart.id,
                lines: cart.lines.concat([{ item, qty: params.qty }])
            }

            cartStore.set(newCart.id, newCart)

            return newCart
        }
    }
}

const run = async () => {
    broker.createService(item)
    broker.createService(cart)

    await broker.start()

    await broker.call('item.create', { id: 'item-1', price: 100 })
    await broker.call('item.create', { id: 'item-2', price: 200 })

    const r1 = await broker.call('item.find', { id: 'item-1' })
    console.log(r1)

    await broker.call('cart.create', { id: 'cart-1' })

    const r2 = await broker.call('cart.addItem', { id: 'cart-1', item: 'item-1', qty: 2 })
    console.log(JSON.stringify(r2))

    const r3 = await broker.call('cart.addItem', { id: 'cart-1', item: 'item-2', qty: 3 })
    console.log(JSON.stringify(r3))

    const r4 = await broker.call('cart.addItem', { id: 'cart-1', item: 'item-n', qty: 3 })
    console.log(r4)
}

run().catch(err => console.error(err))

