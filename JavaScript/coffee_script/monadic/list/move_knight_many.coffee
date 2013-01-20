monadic = require 'monadic'

listM = monadic.list()

moveKnight = (p) ->
	[
		{c: p.c + 2, r: p.r - 1}, {c: p.c + 2, r: p.r + 1},
		{c: p.c - 2, r: p.r - 1}, {c: p.c - 2, r: p.r + 1},
		{c: p.c + 1, r: p.r - 2}, {c: p.c + 1, r: p.r + 2},
		{c: p.c - 1, r: p.r - 2}, {c: p.c - 1, r: p.r + 2}
	].filter (t) -> t.c in [1..8] && t.r in [1..8]

inMany = (num, start) ->
	[1..num].map( (a) -> moveKnight ).reduceRight (acc, elem) ->
		listM.mbind(acc, elem)
	, [start]


console.log inMany 3, {c: 6, r: 2}

canReachInMany = (num, start, end) ->
	inMany(num, start).some (p) ->
		p.c == end.c && p.r == end.r

console.log canReachInMany 3, {c: 6, r: 2}, {c: 6, r: 1}
console.log canReachInMany 3, {c: 6, r: 2}, {c: 7, r: 3}
