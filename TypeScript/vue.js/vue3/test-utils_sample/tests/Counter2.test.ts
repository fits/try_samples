import { mount } from '@vue/test-utils'
import Counter2 from '../src/components/Counter2.vue'

test('count up', () => {
    const counter = mount(Counter2)

    expect(counter.vm.count).toBe(0)

    counter.get('button').trigger('click')

    expect(counter.vm.count).toBe(1)
})
