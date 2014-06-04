-module(gini_impurity).
-export([main/1]).

groupBy(Xs) -> lists:foldr(fun(X, Acc) -> dict:append(X, X, Acc) end, dict:new(), Xs).

countBy(Xs) -> dict:map( fun(K, V) -> length(V) end, groupBy(Xs) ).

gini1(Xs) -> 1 - dict:fold(fun(K, V, Acc) -> Acc + math:pow(V / length(Xs), 2) end, 0, countBy(Xs)).

gini1a(Xs) -> 1 - lists:sum([ math:pow(V / length(Xs), 2) || {_, V} <- dict:to_list(countBy(Xs)) ]).

gini2(Xs) -> lists:sum([ (V1 / length(Xs)) * (V2 / length(Xs)) || {V1, V2} <- combinationProb(dict:to_list(countBy(Xs))) ]).

combinationProb(Xs) -> [ {VX, VY} || {KX, VX} <- Xs, {KY, VY} <- Xs, KX /= KY ].
main(_) ->
	List = ["A", "B", "B", "C", "B", "A"],

	io:format("~p~n", [List]),
	io:format("~p~n", [groupBy(List)]),
	io:format("~p~n", [countBy(List)]),
	io:format("~p~n", [gini1(List)]),
	io:format("~p~n", [gini1a(List)]),
	io:format("~p~n", [combinationProb(dict:to_list(countBy(List)))]),
	io:format("~p~n", [gini2(List)]).
