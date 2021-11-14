
const clone = <T>(obj: T): T => JSON.parse(JSON.stringify(obj))

type CatalogId = string
type Price = number
type Quantity = number
type Amount = number

export interface BillingLine {
    name: string
    unitAmount: Amount
    qty: Quantity
    subTotal: Amount
}

export interface Billing {
    total: Amount
    lines: BillingLine[]
}

interface OrderItem {
    item: CatalogId
    price: Price
    qty: Quantity
}

interface OrderStarted<B, S> {
    tag: 'order.started'
    buyer: B
    seller: S
}

interface OrderItemAdded {
    tag: 'order.item.added'
    item: CatalogId
    price: Price
    qty: Quantity
}

interface OrderItemRemoved {
    tag: 'order.item.removed'
    item: CatalogId
}

interface OrderItemUpdated {
    tag: 'order.item.updated'
    item: CatalogId
    price: Price
    qty: Quantity
}

interface PaymentSelected<P> {
    tag: 'order.payment.selected'
    method: P
    fee: Price
}

interface DeliverySelected<D> {
    tag: 'order.delivery.selected'
    method: D
    fee: Price
}

interface BillingConfirmed {
    tag: 'order.billing.confirmed'
    billing: Billing
}

interface OrderConfirmed {
    tag: 'order.confirmed'
}

interface OrderCompleted {
    tag: 'order.completed'
}

interface OrderFailed {
    tag: 'order.failed'
}

export type OrderEvent<B, S, P, D> = 
    OrderStarted<B, S> |
    OrderItemAdded | OrderItemRemoved | OrderItemUpdated |
    DeliverySelected<D> | 
    PaymentSelected<P> | 
    BillingConfirmed | 
    OrderConfirmed | 
    OrderCompleted | OrderFailed

interface NoneOrder {
    tag: 'none-order'
}

interface OrderBase<B, S> {
    readonly buyer: B
    readonly seller: S
    readonly items: OrderItem[]
}

interface ConfirmOrderBase<B, S, P, D> extends OrderBase<B, S> {
    readonly payment: Payment<P>
    readonly delivery: Delivery<D>
    readonly billing: Billing
}

interface DraftOrder<B, S, P, D> extends OrderBase<B, S> {
    tag: 'draft-order'
    readonly payment?: Payment<P>
    readonly delivery?: Delivery<D>
    readonly billing?: Billing
}

interface ConfirmableOrder<B, S, P, D> extends ConfirmOrderBase<B, S, P, D> {
    tag: 'confirmable-order'
}

interface ConfirmedOrder<B, S, P, D> extends ConfirmOrderBase<B, S, P, D> {
    tag: 'confirmed-order'
}

interface CompletedOrder<B, S, P, D> extends ConfirmOrderBase<B, S, P, D> {
    tag: 'completed-order'
}

interface FailedOrder<B, S, P, D> extends ConfirmOrderBase<B, S, P, D> {
    tag: 'failed-order'
}

export type Order<B, S, P, D> = 
    NoneOrder | 
    DraftOrder<B, S, P, D> | 
    ConfirmableOrder<B, S, P, D> | 
    ConfirmedOrder<B, S, P, D> |
    CompletedOrder<B, S, P, D> | 
    FailedOrder<B, S, P, D>

interface Payment<PaymentMethod> {
    method: PaymentMethod
    fee: Price
}

interface Delivery<DeliveryMethod> {
    method: DeliveryMethod
    fee: Price
}

export class OrderUtil {
    static none(): NoneOrder {
        return { tag: 'none-order' }
    }
}

type OrderEventResult<B, S, P, D> = OrderEvent<B, S, P, D> | null
type FindPrice = (item: CatalogId) => Price | null

export class OrderAction {
    static start<B, S, P, D>(state: Order<B, S, P, D>, 
        buyer: B, seller: S): OrderEventResult<B, S, P, D> {

        switch (state.tag) {
            case 'none-order':
                return { tag: 'order.started', buyer, seller }
        }

        return null
    }

    static changeItem<B, S, P, D>(state: Order<B, S, P, D>, 
        item: CatalogId, qty: Quantity, finder: FindPrice): OrderEventResult<B, S, P, D> {

        switch (state.tag) {
            case 'draft-order':
            case 'confirmable-order':
            case 'failed-order':

                const trg = state.items.find(i => i.item == item)

                if (trg) {
                    if (qty <= 0) {
                        return { tag: 'order.item.removed', item }
                    }
                    else if (trg.qty != qty) {
                        const price = finder(item)

                        if (price != null) {
                            return {
                                tag: 'order.item.updated', 
                                item, price, qty
                            }
                        }
                    }
                }
                else if (qty > 0) {
                    const price = finder(item)

                    if (price != null) {
                        return {
                            tag: 'order.item.added', 
                            item, price, qty
                        }
                    }
                }
        }

        return null
    }

    static selectPayment<B, S, P, D>(state: Order<B, S, P, D>, 
        method: P, fee?: Price): OrderEventResult<B, S, P, D> {

        fee = fee ?? 0

        if (fee >= 0) {
            switch (state.tag) {
                case 'draft-order':
                case 'confirmable-order':
                case 'failed-order':
                    return { tag: 'order.payment.selected', method, fee }
            }
        }

        return null
    }

    static selectDelivery<B, S, P, D>(state: Order<B, S, P, D>, 
        method: D, fee?: Price): OrderEventResult<B, S, P, D> {

        fee = fee ?? 0

        if (fee >= 0) {
            switch (state.tag) {
                case 'draft-order':
                case 'confirmable-order':
                case 'failed-order':
                    return { tag: 'order.delivery.selected', method, fee }
            }
        }

        return null
    }

    static confirmBilling<B, S, P, D>(state: Order<B, S, P, D>, 
        billing: Billing): OrderEventResult<B, S, P, D> {

        if (state.tag == 'draft-order' &&
            state.billing == undefined) {

            return { tag: 'order.billing.confirmed', billing }
        }

        return null
    }

    static confirmOrder<B, S, P, D>(state: Order<B, S, P, D>): OrderEventResult<B, S, P, D> {
        if (state.tag == 'confirmable-order') {
            return { tag: 'order.confirmed' }
        }

        return null
    }

    static completeOrder<B, S, P, D>(state: Order<B, S, P, D>): OrderEventResult<B, S, P, D> {
        if (state.tag == 'confirmed-order') {
            return { tag: 'order.completed' }
        }

        return null
    }

    static failOrder<B, S, P, D, C>(state: Order<B, S, P, D>): OrderEventResult<B, S, P, D> {
        if (state.tag == 'confirmed-order') {
            return { tag: 'order.failed' }
        }

        return null
    }
}

export class OrderRestore {
    static restore<B, S, P, D>(state: Order<B, S, P, D>, 
        events: OrderEvent<B, S, P, D>[]): Order<B, S, P, D> {

        return events.reduce(OrderRestore.apply, state)
    }

    private static apply<B, S, P, D>(state: Order<B, S, P, D>, 
        event: OrderEvent<B, S, P, D>): Order<B, S, P, D> {

        switch (event.tag) {
            case 'order.started':
                if (state.tag == 'none-order') {
                    const { buyer, seller } = event

                    return { 
                        tag: 'draft-order', 
                        buyer, 
                        seller, 
                        items: []
                    }
                }
                break
            case 'order.item.added':
            case 'order.item.updated':
                switch (state.tag) {
                    case 'draft-order':
                    case 'confirmable-order':
                    case 'failed-order':
                        const { item, price, qty } = event

                        const items = clone(state.items).filter(i => i.item != item)
    
                        const isProblem =
                            (event.tag == 'order.item.added') ? 
                                state.items.length != items.length : 
                                state.items.length == items.length
    
                        if (isProblem) {
                            return state
                        }
    
                        items.push({ item, price, qty })
    
                        return {
                            ...state, 
                            tag: 'draft-order',
                            items, 
                            billing: undefined
                        }
                }
                break
            case 'order.item.removed':
                switch (state.tag) {
                    case 'draft-order':
                    case 'confirmable-order':
                    case 'failed-order':
                        const { item } = event
                        const items = clone(state.items).filter(i => i.item != item)

                        return {
                            ...state, 
                            tag: 'draft-order',
                            items, 
                            billing: undefined
                        }
                }
                break
            case 'order.payment.selected':
                switch (state.tag) {
                    case 'draft-order':
                    case 'confirmable-order':
                    case 'failed-order':
                        const { tag, ...payment } = event

                        return {
                            ...state, 
                            tag: 'draft-order',
                            payment, 
                            billing: undefined
                        }
                }
                break
            case 'order.delivery.selected':
                switch (state.tag) {
                    case 'draft-order':
                    case 'confirmable-order':
                    case 'failed-order':
                        const { tag, ...delivery } = event

                        return {
                            ...state, 
                            tag: 'draft-order',
                            delivery, 
                            billing: undefined
                        }
                }
                break
            case 'order.billing.confirmed':
                if (state.tag == 'draft-order') {
                    const { billing } = event

                    if (state.items.length > 0 &&
                        state.payment != undefined &&
                        state.delivery != undefined) {

                        return {
                            ...state,
                            tag: 'confirmable-order',
                            payment: state.payment,
                            delivery: state.delivery,
                            billing
                        }
                    }

                    return { ...state, billing }
                }
                break
            case 'order.confirmed':
                if (state.tag == 'confirmable-order') {
                    return { ...state, tag: 'confirmed-order' }
                }
                break
            case 'order.completed':
                if (state.tag == 'confirmed-order') {
                    return { ...state, tag: 'completed-order' }
                }
                break
            case 'order.failed':
                if (state.tag == 'confirmed-order') {
                    return { ...state, tag: 'failed-order' }
                }
                break
        }

        return state
    }
}
