
const a = {value: 'integer', note: 'string'}

console.log( Object.entries(a).map( ([k, v]) => `${k}-${v}`) )

console.log( Object.fromEntries([['value', {type: 'integer'}], ['note', {type: 'string'}]]) )

console.log( Object.fromEntries(
	Object.entries(a).map( ([k, v]) => [k, {type: v}])
) )
