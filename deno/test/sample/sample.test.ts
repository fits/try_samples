
import { assertEquals } from 'https://deno.land/std@0.109.0/testing/asserts.ts'
import { plusNum, plusStr } from './sample.ts'

Deno.test('1 plusNum 2 is 3', () => {
    const res = plusNum(1, 2)
    assertEquals(res, 3)
})

Deno.test('a plusStr b is ab', () => {
    const res = plusStr('a', 'b')
    assertEquals(res, 'ab')
})
