-module(gini_impurity).
-export([main/1]).

groupBy(Xs) -> lists:foldr(fun(X, Acc) -> dict:append(X, X, Acc) end, dict:new(), Xs).

countBy(Xs) -> dict:map( fun(K, V) -> length(V) end, groupBy(Xs) ).

gini1(Xs) -> 1 - dict:fold(fun(K, V, Acc) -> Acc + math:pow(V / length(Xs), 2) end, 0, countBy(Xs)).

main(_) ->
	List = ["A", "B", "B", "C", "B", "A"],

	io:format("~p~n", [List]),
	io:format("~p~n", [groupBy(List)]),
	io:format("~p~n", [countBy(List)]),
	io:format("~p~n", [gini1(List)]).
