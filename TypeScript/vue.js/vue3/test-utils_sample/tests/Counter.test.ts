import { mount } from '@vue/test-utils'
import Counter from '../src/components/Counter.vue'

test('count up', async () => {
    const counter = mount(Counter)

    expect(counter.vm.count).toBe(0)

    await counter.get('button').trigger('click')

    expect(counter.vm.count).toBe(1)
})
