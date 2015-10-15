
import Data.Either

main = do
    print (return 1 :: Either () Int)
    print (Right 2 :: Either () Int)

    print (Left "err" :: Either String ())

    print $ either id id $ Right 3
    print $ either id id $ Left "err"

    print $ either id (+2) $ Right 3

    print $ (Right 1 :: Either () Int) >>= \a -> Right (a + 10)
