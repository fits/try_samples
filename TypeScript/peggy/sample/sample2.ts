import * as peggy from 'peggy'

const grammar = `
expr
  = left:term right:(_ 'OR' _ term)*
    {
      if (right.length == 0) {
        return left
      }

      const rs = right.map(r => r.slice(-1)[0])

      return {
        or: [left].concat(rs)
      }
    }

term
  = left:factor right:(_ 'AND' _ factor)*
    {
      if (right.length == 0) {
        return left
      }

      const rs = right.map(r => r.slice(-1)[0])

      return {
        and: [left].concat(rs)
      }
    }

factor
  = leftParen _ e:expr _ rightParen
    {
      return e
    }
  / c:char+
    {
      return c.join('')
    }

leftParen = '('
rightParen = ')'

char = $[a-z]i

_ "whitespace"
  = [ ]*
    {
      return ''
    }
`

const parser = peggy.generate(grammar)

const showParse = (q: string) => {
  const r = parser.parse(q)
  console.log(JSON.stringify(r, null, 2))
}

showParse('a')
showParse('abCD')

showParse('(abCD)')

showParse('ab AND cd')
showParse('ab AND cd AND ef AND gh')

showParse('ab OR cd')

showParse('ab AND cd OR ef')
showParse('ab AND cd OR ef AND gh')

showParse('ab AND (cd OR ef OR gh)')

showParse('(ab AND cd) OR ( ef AND gh OR ij )')
