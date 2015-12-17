
var p1 = new Promise( (resolve, reject) =>
	resolve("sample")
);

p1.then( (v) => console.log(`success: ${v}`) );

var p2 = new Promise( (resolve, reject) =>
	reject("not found")
);

p2.then(
	(v) => console.log(`success: ${v}`),
	(e) => console.error(`error: ${e}`)
);

Promise.resolve('aaa').then(
	(v) => console.log(`success: ${v}`)
).catch(
	(e) => console.error(`error: ${e}`)
);

Promise.reject('bbb').then(
	(v) => console.log(`success: ${v}`)
).catch(
	(e) => console.error(`error: ${e}`)
);

/* NG: Node.js v5.1.0 (SyntaxError: Unexpected token throw)
new Promise( (resolve, reject) => throw "bad" )
*/

new Promise( (resolve, reject) => {
	throw "bad"
}).then(
	(v) => console.log(`success: ${v}`)
).catch(
	(e) => console.error(`error: ${e}`)
);
