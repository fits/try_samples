import Control.Monad.Free

data Greeting a next = Hello a next | Bye
	deriving (Show)

instance Functor (Greeting a) where
	fmap f (Hello a next) = Hello a (f next)
	fmap f Bye = Bye

liftF :: (Functor f) => f r -> Free f r
liftF f = Impure (fmap Pure f)

hello :: a -> Free (Greeting a) ()
hello x = liftF (Hello x ())

bye :: Free (Greeting a) r
bye = liftF Bye

sampleData :: Free (Greeting [Char]) ()
sampleData = do
	hello "one"
	hello "two"
	hello "three"
	bye

showData :: (Show a, Show r) => Free (Greeting a) r -> String
showData (Impure (Hello a next)) = "hello : " ++ show a ++ "\n" ++ showData next
showData (Impure Bye) = "bye\n"
showData (Pure r) = "return " ++ show r

main = do
	print $ hello "sample"
	print $ sampleData

	putStrLn "-----"
	putStrLn $ showData (hello "sample")
	putStrLn "-----"
	putStrLn $ showData sampleData
