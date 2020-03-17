import { plusNum, plusStr } from './sample'

it('1 plusNum 2 = 3', () => {
    expect(plusNum(1, 2)).toBe(3)
})

it('a plusStr b = ab', () => {
    const res = plusStr('a', 'b')
    expect(res).toBe('ab')
})
