-module(gini).
-export([main/1]).

groupBy(Xs) -> lists:foldr(fun(X, Acc) -> dict:append(X, X, Acc) end, dict:new(), Xs).

countBy(Xs) -> dict:map( fun(_, V) -> length(V) end, groupBy(Xs) ).

% (a) 1 - (AA + BB + CC)
giniA(Xs) -> 1 - lists:sum([ math:pow(V / length(Xs), 2) || {_, V} <- dict:to_list(countBy(Xs)) ]).

combinationProb(Xs) -> [ {Vx, Vy} || {Kx, Vx} <- Xs, {Ky, Vy} <- Xs, Kx /= Ky ].
% (b) AB + AC + BA + BC + CA + CB
giniB(Xs) -> lists:sum([ (Vx / length(Xs)) * (Vy / length(Xs)) || {Vx, Vy} <- combinationProb(dict:to_list(countBy(Xs))) ]).

main(_) ->
	List = ["A", "B", "B", "C", "B", "A"],

	io:format("~p~n", [ giniA(List) ]),
	io:format("~p~n", [ giniB(List) ]).
