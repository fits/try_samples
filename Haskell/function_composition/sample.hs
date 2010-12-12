plus x = x + 3
times x = x * 2

f = times . plus
g = plus . times

main = do
    -- times(plus 4)
    putStrLn $ show $ f 4
    -- plus(times 4)
    putStrLn $ show $ g 4
    
    putStrLn $ show $ plus $ times 4
    putStrLn $ show $ plus(times 4)
    