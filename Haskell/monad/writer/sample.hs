import Control.Monad.Writer

logNum :: Int -> Int -> Writer [Int] Int
logNum n x = writer (x + n, [x])

main = do
    print $ runWriter (return 1 :: Writer [Int] Int)
    print $ runWriter $ (return 1 :: Writer [Int] Int) >>= logNum 2 >>= logNum 3
    print $ execWriter $ (return 1 :: Writer [Int] Int) >>= logNum 2 >>= logNum 3