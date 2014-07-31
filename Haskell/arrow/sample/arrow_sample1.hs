
import Control.Arrow

main = do
	let plus = arr $ (+) 3
	-- 22
	print $ plus 4

	let times = arr $ (*) 2
	print $ times 3

	-- (4 + 3) * 2 = 14
	let f = plus >>> times
	print $ f 4

	-- (3 + 3, 5 * 2) = (6, 10)
	print $ plus *** times $ (3, 5)

	-- (4 + 3, 4 * 2) = (7, 8)
	print $ plus &&& times $ 4
