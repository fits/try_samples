import { mount } from '@vue/test-utils'
import ItemList from '../src/components/ItemList.vue'

globalThis.fetch = jest.fn((_input: RequestInfo, _init?: RequestInit | undefined) => {
    return Promise.resolve({
        json: () => Promise.resolve([ { name: 'test', value: 1} ])
    } as Response)
})

beforeEach(() => {
    jest.clearAllMocks()
})

test('load items', async () => {
    const w = mount(ItemList)

    expect(w.vm.items).toHaveLength(0)

    await w.find('button').trigger('click')

    expect(fetch).toHaveBeenCalledTimes(1)
    
    expect(w.vm.items).toHaveLength(1)
    expect(w.vm.items[0].name).toBe('test')
})
