-module(map_sample).
-export([main/1]).

-record(item, { name, price = 0 }).

main(_) ->
	ItemList = [
		#item{name = "item1", price = 100}, 
		#item{name = "item2", price = 50}, 
		#item{name = "item3", price = 200}, 
		#item{name = "item4"}
	],

	lists:foreach(
		fun(X) -> io:format("result = ~p~n", [X]) end, 
		lists:map(fun(X) -> X#item.price * 3.5 end, ItemList)
	).
