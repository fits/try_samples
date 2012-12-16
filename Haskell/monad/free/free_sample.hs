import Control.Monad.Free

data Greeting a next = Hello a next | Bye
	deriving (Show)

instance Functor (Greeting a) where
	fmap f (Hello a next) = Hello a (f next)
	fmap f Bye = Bye

hello :: a -> Free (Greeting a) ()
hello x = Impure (Hello x (Pure ()))

bye :: Free (Greeting a) r
bye = Impure Bye

sampleData :: Free (Greeting [Char]) ()
sampleData = do
	hello "one"
	hello "two"
	hello "three"
	bye

main = do
	print $ hello "sample"
	print $ sampleData
