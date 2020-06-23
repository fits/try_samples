
import * as R from 'https://deno.land/x/ramda/source/index.js'
import map from 'https://deno.land/x/ramda/source/map.js'

const f = R.map(x => x * 2)

console.log( f([1, 2, 3]) )
console.log( f([4, 5]) )

console.log( map(x => x * 3, [1, 2, 3]) )
