
plus x = x + 3
times x = x * 2

f = times . plus
g = plus . times

main = do
    -- times(plus(4)) = 14
    putStrLn $ show $ f 4
    -- plus(times(4)) = 11
    putStrLn $ show $ g 4
