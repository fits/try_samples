-module(sample).
-export([main/1]).

main(_) ->
	List = ["A", "B", "C"],

	Clist = [ {X, Y} || X <- List, Y <- List, X /= Y ],

	io:format("~p~n~p~n", [List, Clist]).
