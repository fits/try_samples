select
	*
from
	orders od
	join order_items oi on
		od.order_id = oi.order_id
	,
	users us
where
	od.ordered_date > ? and
	od.user_id = us.user_id
