
-- パターンマッチの定義 True の場合は t、False の場合は f が実行される
test True t f = t
test False t f = f

a 0 = 1
a n = n * a(n - 1)

main = do
	putStrLn(test (1 == 1) "yes" "no")
	putStrLn(test (1 < 0) "yes" "no")

	let res = a 5
	putStrLn(show res)

