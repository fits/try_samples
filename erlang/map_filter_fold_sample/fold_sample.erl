-module(fold_sample).
-export([main/1]).

-record(item, { name, price = 0 }).

main(_) ->
	ItemList = [
		#item{name = "item1", price = 100}, 
		#item{name = "item2", price = 50}, 
		#item{name = "item3", price = 200}, 
		#item{name = "item4"}
	],

	Res = lists:foldl(fun(X, Sum) -> X#item.price + Sum  end, 0, ItemList),

	io:format("result = ~p~n", [Res]).
