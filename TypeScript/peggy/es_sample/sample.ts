import * as peggy from 'peggy'

const parser = peggy.generate(`
start
  = left:query right:(_ 'OR'i _ query)*
    {
      if (right.length == 0) {
        return left
      }

      const should = [left].concat(
        right.flat().flat().filter(r => r && typeof r === 'object')
      )

      return {
        bool: {
          should
        }
      }
    }

query
  = s:('+' / '-')? t:term
    {
      if (s !== '-') {
        return t
      }

      return {
        bool: {
          must_not: t
        }
      }
    }

term
  = left:word ':' right:word
    {
      if (right.match(/[*?]/)) {
        return {
          wildcard: {
            [left]: {
              value: right
            }
          }
        }
      }

      return {
        term: { [left]: right }
      }
    }
  / left:word ':' lb:('{' / '[') lv:number _ 'TO' _ rv:number rb:('}' / ']')
    {
      const cond = [
        [ (lb == '[') ? 'gte' : 'gt', lv ],
        [ (rb == ']') ? 'lte' : 'lt', rv ]
      ]

      return {
        range: {
          [left]: Object.fromEntries(cond.filter(([_, v]) => v != '*'))
        }
      }
    }

number = $[0-9*]+
word = $[a-z0-9_*?-]i+
_ = $[ ]+

`)

const printParse = (exp: string) => {
  const res = parser.parse(exp)

  console.log(JSON.stringify(res, null, 2))
}

printParse('test:a12')
printParse('+test:a12')

printParse('-test:a12')

printParse('test:a1 OR test:b2')

printParse('test:a1 OR  test:b2  OR test:c3')

printParse('test:a1-* OR test:b2 OR -test:c3')

printParse('test:[1 TO 10}')
printParse('test:{5 TO  *]')