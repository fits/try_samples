import Text.ParserCombinators.Parsec

csvFile = endBy line eol
line = sepBy cell (char ',')
cell = quotedCell <|> many (noneOf ",\n")
eol = char '\n'
quotedCell = do 
	char '"'
	content <- many quotedChar
	char '"'
	return content

quotedChar = noneOf "\"" <|> try (string "\"\"" >> return '"')

main = do
	cs <- getContents
	let res = parse csvFile "" cs

	case res of
		Left err -> print err
		Right x -> putStrLn $ show x

