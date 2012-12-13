import Control.Monad.Free

data Greeting a n = Hello a n | Bye
	deriving (Show)

instance Functor (Greeting a) where
	fmap f (Hello a n) = Hello a (f n)
	fmap f Bye = Bye

main = do
	print $ Impure (Hello "sample" (Pure ()))
