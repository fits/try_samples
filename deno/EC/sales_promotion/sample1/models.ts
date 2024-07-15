
export type LineNo = string
export type ItemId = string
export type Amount = { amount: number }
export type Rate = { rate: number }

export interface Item {
    id: ItemId,
    attrs?: Map<string, string>,
}

export interface OrderItem {
    lineNo: LineNo,
    item: Item,
    price: number,
}

export interface ItemCondition {
    attrs: Map<string, Array<string>>,
}

export type Condition = ItemCondition

export interface ItemDiscountAction {
    discount: Amount | Rate,
    target?: ItemCondition,
}

export type RewardAction = ItemDiscountAction

export interface Discount<T> {
    discount: number,
    target: T,
}

export type Reward<T> = Discount<T>

export function isRate(v: Amount | Rate): v is Rate {
    return 'rate' in v
}

export function isMatch<T extends OrderItem>(cond: Condition, target: T) {
    for (const [k, v] of cond.attrs) {
        if (v.length == 0) {
            continue
        }

        const a = target.item.attrs?.get(k)

        if (a === undefined || !v.includes(a)) {
            return false
        }

    }

    return true
}

export function action<T extends OrderItem>(action: RewardAction, targets: T[]): Reward<T>[] {
    if (isRate(action.discount)) {
        if (action.discount.rate <= 0 || action.discount.rate > 1) {
            throw new Error(`invalid discount rate: ${action.discount.rate}`)
        }
    }
    else if (action.discount.amount <= 0) {
        throw new Error(`invalid discount amaount: ${action.discount.amount}`)
    }

    if (action.target !== undefined) {
        targets = targets.filter(t => isMatch(action.target!, t))
    }

    return targets.map(t => {
        if (isRate(action.discount)) {
            return { discount: action.discount.rate * t.price, target: t }
        }

        return { discount: Math.min(action.discount.amount, t.price), target: t }
    })
}

