-module(compose_sample).
-export([main/1]).

compose(F, G) -> fun(X) -> F(G(X)) end.

main(_) ->
	Plus = fun(X) -> X + 3 end,
	Times = fun(X) -> X * 2 end,

	F = compose(Times, Plus),

	io:format("result = ~p~n", [F(4)]).
