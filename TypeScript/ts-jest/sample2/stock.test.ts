import * as S from './stock'

it('create by empty item', () => {
    const service = new S.SampleStockService()
    const stock = service.create('')

    expect(stock).toBeUndefined()
})

it('create by non-empty item', () => {
    const service = new S.SampleStockService()
    const stock = service.create('item1')

    expect(stock?.item).toBe('item1')
    expect(stock?.qty).toBe(0)
})

it('find created', () => {
    const service = new S.SampleStockService()
    service.create('item1')

    const stock = service.find('item1')

    expect(stock?.item).toBe('item1')
    expect(stock?.qty).toBe(0)
})

it('find not created', () => {
    const service = new S.SampleStockService()

    const stock = service.find('invalid-item')

    expect(stock).toBeUndefined()
})

it('update 3 + 2 - 4', () => {
    const service = new S.SampleStockService()
    service.create('item1')

    const st1 = service.update('item1', 3)
    const st2 = service.update('item1', 2)
    const st3 = service.update('item1', -4)

    expect(st1?.qty).toBe(3)
    expect(st2?.qty).toBe(5)
    expect(st3?.qty).toBe(1)
})

it('update 2 - 3', () => {
    const service = new S.SampleStockService()
    service.create('item1')

    const st1 = service.update('item1', 2)
    const st2 = service.update('item1', -3)

    expect(st1?.qty).toBe(2)
    expect(st2).toBeUndefined()
})
