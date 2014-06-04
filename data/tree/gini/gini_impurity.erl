-module(gini_impurity).
-export([main/1]).

groupBy(Xs) -> lists:foldr(fun(X, Acc) -> dict:append(X, X, Acc) end, dict:new(), Xs).

countBy(Xs) -> dict:map( fun(K, V) -> length(V) end, groupBy(Xs) ).

gini1(Xs) -> 1 - dict:fold(fun(K, V, Acc) -> Acc + math:pow(V / length(Xs), 2) end, 0, countBy(Xs)).

gini1a(Xs) -> 1 - lists:sum([ math:pow(V / length(Xs), 2) || {_, V} <- dict:to_list(countBy(Xs)) ]).

gini2(Xs) -> lists:sum([ (Vx / length(Xs)) * (Vy / length(Xs)) || {Vx, Vy} <- combinationProb(dict:to_list(countBy(Xs))) ]).

combinationProb(Xs) -> [ {Vx, Vy} || {Kx, Vx} <- Xs, {Ky, Vy} <- Xs, Kx /= Ky ].
main(_) ->
	List = ["A", "B", "B", "C", "B", "A"],

	io:format("~p~n", [List]),
	io:format("~p~n", [groupBy(List)]),
	io:format("~p~n", [countBy(List)]),
	io:format("~p~n", [gini1(List)]),
	io:format("~p~n", [gini1a(List)]),
	io:format("~p~n", [combinationProb(dict:to_list(countBy(List)))]),
	io:format("~p~n", [gini2(List)]).
