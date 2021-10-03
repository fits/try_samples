
import { assertEquals } from 'https://deno.land/std@0.109.0/testing/asserts.ts'
import { toEs } from './es_convert.ts'

Deno.test('sample:a1', () => {
    const res = toEs('sample:a1')

    assertEquals(
        res,
        {
            term:{
                sample: 'a1'
            }
        }
    )
})

Deno.test('-category:a1-2', () => {
    const res = toEs('-category:a1-2')

    assertEquals(
        res, 
        {
            bool:{
                must_not: {
                    term: {
                        category: 'a1-2'
                    }
                }
            }
        }
    )
})

Deno.test('sample:a*', () => {
    const res = toEs('sample:a*')

    assertEquals(
        res, 
        {
            wildcard:{
                sample: {
                    value: 'a*'
                }
            }
        }
    )
})

Deno.test('-sample:a*', () => {
    const res = toEs('-sample:a*')

    assertEquals(
        res, 
        {
            bool: {
                must_not: {
                    wildcard:{
                        sample: {
                            value: 'a*'
                        }
                    }
                }
            }
        }
    )
})

Deno.test('num:[1 TO 5]', () => {
    const res = toEs('num:[1 TO 5]')

    assertEquals(
        res, 
        {
            range: {
                num: {
                    gte: 1,
                    lte: 5
                }
            }
        }
    )
})

Deno.test('num:{1 TO 5}', () => {
    const res = toEs('num:{1 TO 5}')

    assertEquals(
        res, 
        {
            range: {
                num: {
                    gt: 1,
                    lt: 5
                }
            }
        }
    )
})

Deno.test('num:[1 TO *]', () => {
    const res = toEs('num:[1 TO *]')

    assertEquals(
        res, 
        {
            range: {
                num: {
                    gte: 1
                }
            }
        }
    )
})

Deno.test('num:[* TO 5}', () => {
    const res = toEs('num:[* TO 5}')

    assertEquals(
        res, 
        {
            range: {
                num: {
                    lt: 5
                }
            }
        }
    )
})

Deno.test('category:(a1 OR b2 OR c3)', () => {
    const res = toEs('category:(a1 OR b2 OR c3)')

    assertEquals(
        res,
        {
            terms:{
                category: ['a1', 'b2', 'c3']
            }
        }
    )
})

Deno.test('-category:(a1 OR b2)', () => {
    const res = toEs('-category:(a1 OR b2)')

    assertEquals(
        res,
        {
            bool: {
                must_not: {
                    terms:{
                        category: ['a1', 'b2']
                    }
                }
            }
        }
    )
})

Deno.test('category:a1 OR category:b2', () => {
    const res = toEs('category:a1 OR category:b2')

    assertEquals(
        res,
        {
            bool: {
                should: [
                    {
                        term: {
                            category: 'a1'
                        }
                    },
                    {
                        term: {
                            category: 'b2'
                        }
                    },
                ]
            }
        }
    )
})

Deno.test('category:(a1 OR b2) OR -category:c3 OR num:[10 TO 15}', () => {
    const res = toEs('category:(a1 OR b2) OR -category:c3 OR num:[10 TO 15}')

    assertEquals(
        res,
        {
            bool: {
                should: [
                    {
                        terms: {
                            category: ['a1', 'b2']
                        }
                    },
                    {
                        bool: {
                            must_not: {
                                term: {
                                    category: 'c3'
                                }
                            }
                        }
                    },
                    {
                        range: {
                            num: {
                                gte: 10,
                                lt: 15
                            }
                        }
                    }
                ]
            }
        }
    )
})
