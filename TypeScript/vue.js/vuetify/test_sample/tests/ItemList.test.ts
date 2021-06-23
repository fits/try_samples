
import Vuetify from 'vuetify'
import { mount, createLocalVue } from '@vue/test-utils'
import ItemList from '../src/components/ItemList.vue'

globalThis.fetch = jest.fn((_input: RequestInfo, _init?: RequestInit | undefined) => 
  Promise.resolve({
    json: () => Promise.resolve([
      {name: 'test1', value: 1, category: 'T'},
      {name: 'test2', value: 2, category: 'T'}
    ])
  } as Response)
)

beforeEach(() => {
  jest.clearAllMocks()
})

const sleep = async (timeout: number) => 
  new Promise( resolve => {
    setTimeout(resolve, timeout)
  }) 

describe('ItemList', () => {
  const localVue = createLocalVue()

  test('created component', async () => {
      const w = mount(ItemList, {
          localVue, 
          vuetify: new Vuetify()
      })

      await sleep(100)

      expect(globalThis.fetch).toBeCalledTimes(1)
      expect(w.vm.$data.items).toHaveLength(2)
  })
})
