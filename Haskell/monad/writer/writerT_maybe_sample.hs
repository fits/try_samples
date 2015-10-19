import Control.Monad.Writer

type WriterMaybe = WriterT [Int] Maybe Int

logNum :: Int -> Int -> WriterMaybe
logNum n x = WriterT $ return (x + n, [x])

plus :: Int -> Int -> WriterMaybe
plus n x = lift $ return (x + n)

none :: WriterMaybe
none = WriterT Nothing

main = do
    print $ runWriterT (return 1 :: WriterMaybe)
    print $ runWriterT $ (return 1 :: WriterMaybe) >>= logNum 2 >>= logNum 3
    print $ runWriterT $ (return 1 :: WriterMaybe) >>= plus 2 >>= plus 3
    print $ runWriterT $ (return 1 :: WriterMaybe) >>= logNum 2 >>= plus 3
    print $ runWriterT $ none >>= logNum 2 >>= plus 3

