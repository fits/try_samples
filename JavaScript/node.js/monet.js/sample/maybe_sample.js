'use strict';

const Maybe = require('monet').Maybe;

const func = v => Maybe.Some(v + 10);

const d1 = Maybe.Some(2);

console.log( d1 );
console.log( d1.some() );

console.log( d1.bind(func) );
console.log( d1.bind(func).bind(func) );

console.log('-----');

const d2 = Maybe.None();

console.log( d2 );
console.log( d2.orSome('none') );
console.log( d2.bind(func) );
