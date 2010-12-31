
open System
open FParsec.Primitives
open FParsec.CharParsers

let quotedChar = noneOf "\"" <|> attempt (pstring "\"\"" >>. preturn '"')
let quotedCell = pchar '"' >>. manyChars quotedChar .>> pchar '"'
let cell = quotedCell <|> manyChars (noneOf ",\n")
let line = sepBy cell (pchar ',')
let csvFile = sepEndBy line newline


let cs = Console.In.ReadToEnd()
let res = run csvFile cs

match res with
| Success (v, _, _) -> Console.WriteLine(v)
| Failure (msg, _, _) -> Console.WriteLine(msg)
