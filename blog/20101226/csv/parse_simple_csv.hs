import Text.ParserCombinators.Parsec

csvFile = endBy line eol
line = sepBy cell (char ',')
cell = many (noneOf ",\n")
eol = char '\n'

main = do
	cs <- getContents
	let res = parse csvFile "" cs

	case res of
		Left err -> print err
		Right x -> putStrLn $ show x

