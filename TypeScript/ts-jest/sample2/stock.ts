
export type ItemCode = string
export type Quantity = number

export class Stock {
    constructor(
        readonly item: ItemCode,
        readonly qty: Quantity = 0
    ) {}
}

export type StockResult = Stock | undefined

export interface StockService {
    find(item: ItemCode): StockResult
    create(item: ItemCode): StockResult
    update(item: ItemCode, diffQty: Quantity): StockResult
}

export class SampleStockService implements StockService {
    private store: Map<ItemCode, Stock> = new Map<ItemCode, Stock>()

    find(item: ItemCode): StockResult {
        return this.store.get(item)
    }

    create(item: ItemCode): StockResult {
        if (item && item.trim()) {
            const stock = new Stock(item)

            this.store.set(item, stock)

            return stock
        }
        return undefined
    }

    update(item: ItemCode, diffQty: Quantity): StockResult {
        const stock = this.find(item)

        if (stock && stock.qty + diffQty >= 0) {
            const newStock = new Stock(stock.item, stock.qty + diffQty)
            
            this.store.set(item, newStock)

            return newStock
        }

        return undefined
    }
}