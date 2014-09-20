select
	us.user_id,
	us.name,
	(select count(*) from REFUNDS rf where rf.user_id = us.user_id) as refund_count1,
	(select count(*) from SCH1.REFUNDS rf where rf.user_id = us.user_id) as refund_count2
from
	Users us
where
	us.user_id in (
		select
			ou.user_id
		from (
			select
				od.user_id,
				count(*) ct
			from
				orders od
				join order_items oi on
					od.item_id = oi.item_id
			where
				oi.item_id in (
					select item_id from special_items
				) and
				od.ordered_date > ?
			group by
				od.user_id
		) ou
		where
			ou.ct >= 5
	)
