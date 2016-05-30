
import Data.List (unfoldr)

f n = if n > 0 then Just (1, n - 1) else Nothing

main = do
    let d = 5

    print $ unfoldr f d
