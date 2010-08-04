
data Stack a = Empty | Push a (Stack a)

isEmpty :: Stack a -> Bool
isEmpty Empty = True
isEmpty (Push _ _) = False

top :: Stack a -> a
top (Push a _) = a

pop :: Stack a -> Stack a
pop (Push _ stk) = stk

main = do
	let e = Empty
	let t1 = Push "test" (Empty)
	let t2 = Push "test2" (Push "test2-1" (Empty))

	-- True と出力
	print $ isEmpty e
	-- False と出力
	print $ isEmpty t1
	-- False と出力
	print $ isEmpty t2

	-- "test" と出力
	print $ top t1
	-- "test2-1" と出力
	print $ top (pop t2)
