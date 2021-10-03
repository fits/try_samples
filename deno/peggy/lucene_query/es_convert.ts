import peggy from 'https://cdn.skypack.dev/peggy'

const grammar = `
query
  = l:clause r:(_ 'OR' _ clause)*
    {
      if (r.length == 0) {
        return l
      }

      return {
        bool: {
          should: [l].concat( r.map(v => v.slice(-1)[0]) )
        }
      }
    }

clause
  = p:('+' / '-')? t:term
    {
      if (p != '-') {
        return t
      }

      return {
        bool: {
          must_not: t
        }
      }
    }

term
  = f:field ':' l:('[' / '{') _ s:rangeNum _ 'TO' _ e:rangeNum _ r:(']' / '}')
    {
      const cs = [
        [ (l == '{') ? 'gt' : 'gte', s ],
        [ (r == '}') ? 'lt' : 'lte', e ]
      ].filter( ([_, v]) => v != '*' )

      return {
        range: {
          [f]: Object.fromEntries(cs)
        }
      }
    }
  / f:field ':' '(' _ l:value r:(_ 'OR' _ value)* _ ')'
    {
      const cs = [l].concat(r.map(v => v.slice(-1)[0]))

      return {
        terms: {
          [f]: cs
        }
      }
    }
  / f:field ':' v:value
    {
      if (v.match(/[*?]/)) {
        return {
          wildcard: {
            [f]: {
              value: v
            }
          }
        }
      }

      return {
        term: {
          [f]: v
        }
      }
    }

field
  = $[^:]+

value
  = $[^ ()]+

rangeNum
  = n:[0-9]+
    {
      return parseInt(n.join(''))
    }
  / '*'

_ "whitespace"
  = [ ]*
    {
      return {}
    }
`

const parser = peggy.generate(grammar)

export const toEs = (query: string) => parser.parse(query)
