
monadic = require 'monadic'
listM = monadic.list()

moveKnight = (p) ->
	[
		{c: p.c + 2, r: p.r - 1}, {c: p.c + 2, r: p.r + 1},
		{c: p.c - 2, r: p.r - 1}, {c: p.c - 2, r: p.r + 1},
		{c: p.c + 1, r: p.r - 2}, {c: p.c + 1, r: p.r + 2},
		{c: p.c - 1, r: p.r - 2}, {c: p.c - 1, r: p.r + 2}
	].filter (t) -> t.c in [1..8] && t.r in [1..8]

in3 = (start) ->
	listM.mbind(
		listM.mbind(
			listM.mbind(
				[start], 
				moveKnight
			),
			moveKnight
		),
		moveKnight
	)

console.log in3 {c: 6, r: 2}

canReachIn3 = (start, end) ->
	in3(start).some (p) ->
		p.c == end.c && p.r == end.r

console.log canReachIn3 {c: 6, r: 2}, {c: 6, r: 1}
console.log canReachIn3 {c: 6, r: 2}, {c: 7, r: 3}
