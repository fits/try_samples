'use strict';

const List = require('monet').List;

const d1 = List.of(1);

console.log( d1 );

console.log('-----');

const d2 = d1.cons(2).cons(3);

console.log( d2 );
console.log( d2.reverse().head() );

console.log('-----');

const d3 = List.fromArray([5, 6, 7]);

console.log( d3 );

console.log( d3.bind(v => List.of(v + 10)).toArray() );

