import { assert, assertEquals, assertThrows, assertFalse } from 'https://deno.land/std@0.224.0/assert/mod.ts'
import { Amount, Rate, isRate, OrderItem, isMatch, ItemCondition, ItemDiscountAction, action } from './models.ts'

Deno.test('rate is Rate', () => {
    const v: Rate = { rate: 0.5 }
    assert(isRate(v))
})

Deno.test('amount is not Rate', () => {
    const v: Amount = { amount: 100 }
    assertFalse(isRate(v))
})

Deno.test('match single attribute', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['C', 'A']]])
    }

    assert(isMatch(c, v))
})

Deno.test('not match with different upper or lowercase', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['c', 'a']]])
    }

    assertFalse(isMatch(c, v))
})

Deno.test('not match single attribute', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['C', 'B']]])
    }

    assertFalse(isMatch(c, v))
})

Deno.test('match a attribute and empty condition', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A'], ['type', 't1']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['C', 'A']], ['type', []]])
    }

    assert(isMatch(c, v))
})

Deno.test('match multi attributes', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A'], ['type', 't1']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['C', 'A']], ['type', ['t1', 't2']]])
    }

    assert(isMatch(c, v))
})

Deno.test('match a attribute and not match attribute', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A'], ['type', 't3']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([['category', ['C', 'A']], ['type', ['t1', 't2']]])
    }

    assertFalse(isMatch(c, v))
})

Deno.test('match empty attribute condition', () => {
    const v: OrderItem = {
        lineNo: '1',
        item: { id: 'item-1', attrs: new Map([['category', 'A']])},
        price: 100,
    }

    const c: ItemCondition = {
        attrs: new Map([])
    }

    assert(isMatch(c, v))
})


Deno.test('all 10% off', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { rate: 0.1 },
    }

    const rs = action(act, ds)

    assertEquals(3, rs.length)

    assertEquals(100, rs[0].discount)
    assertEquals('1', rs[0].target.lineNo)
    assertEquals(200, rs[1].discount)
    assertEquals('2', rs[1].target.lineNo)
    assertEquals(300, rs[2].discount)
    assertEquals('3', rs[2].target.lineNo)
})

Deno.test('all -100', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { amount: 100 },
    }

    const rs = action(act, ds)

    assertEquals(3, rs.length)

    assertEquals(100, rs[0].discount)
    assertEquals('1', rs[0].target.lineNo)
    assertEquals(100, rs[1].discount)
    assertEquals('2', rs[1].target.lineNo)
    assertEquals(100, rs[2].discount)
    assertEquals('3', rs[2].target.lineNo)
})

Deno.test('discount rate > 1.0', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { rate: 1.1 },
    }

    assertThrows(() => {
        action(act, ds)
    })
})

Deno.test('discount rate <= 0.0', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { rate: 0 },
    }

    assertThrows(() => {
        action(act, ds)
    })
})

Deno.test('discount amount <= 0', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { amount: 0 },
    }

    assertThrows(() => {
        action(act, ds)
    })
})

Deno.test('discount amount > price', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1' }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2' }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3' }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { amount: 4000 },
    }

    const rs = action(act, ds)

    assertEquals(3, rs.length)

    assertEquals(1000, rs[0].discount)
    assertEquals('1', rs[0].target.lineNo)
    assertEquals(2000, rs[1].discount)
    assertEquals('2', rs[1].target.lineNo)
    assertEquals(3000, rs[2].discount)
    assertEquals('3', rs[2].target.lineNo)
})

Deno.test('target items 10% off', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1', attrs: new Map([['category', 'A']]) }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2', attrs: new Map([['category', 'B']]) }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3', attrs: new Map([['category', 'C']]) }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { rate: 0.1 },
        target: { attrs: new Map([['category', ['A', 'C']]]) }
    }

    const rs = action(act, ds)

    assertEquals(2, rs.length)

    assertEquals(100, rs[0].discount)
    assertEquals('1', rs[0].target.lineNo)
    assertEquals(300, rs[1].discount)
    assertEquals('3', rs[1].target.lineNo)
})

Deno.test('discount none target', () => {
    const ds: OrderItem[] = [
        { lineNo: '1', item: { id: 'item-1', attrs: new Map([['category', 'A']]) }, price: 1000 },
        { lineNo: '2', item: { id: 'item-2', attrs: new Map([['category', 'B']]) }, price: 2000 },
        { lineNo: '3', item: { id: 'item-3', attrs: new Map([['category', 'C']]) }, price: 3000 },
    ]

    const act: ItemDiscountAction = {
        discount: { rate: 0.1 },
        target: { attrs: new Map([['category', ['D']]]) }
    }

    const rs = action(act, ds)

    assertEquals(0, rs.length)
})
