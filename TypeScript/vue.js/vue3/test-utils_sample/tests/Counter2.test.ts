import { mount } from '@vue/test-utils'
import Counter2 from '../src/components/Counter2.vue'

test('count up', async () => {
    const counter = mount(Counter2)

    expect(counter.vm.count).toBe(0)

    await counter.get('button').trigger('click')

    expect(counter.vm.count).toBe(1)
})
