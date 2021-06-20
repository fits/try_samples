import { mount } from '@vue/test-utils'
import Counter from '../src/components/Counter.vue'

test('count up', () => {
    const counter = mount(Counter)

    expect(counter.vm.count).toBe(0)

    counter.get('button').trigger('click')

    expect(counter.vm.count).toBe(1)
})
