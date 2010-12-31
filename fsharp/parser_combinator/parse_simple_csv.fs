
open System
open FParsec.Primitives
open FParsec.CharParsers

let line = manyChars (noneOf "\n")
let csvFile = sepEndBy line newline

let cs = Console.In.ReadToEnd()
let res = run csvFile cs

match res with
| Success (v, _, _) -> Console.WriteLine(v)
| Failure (msg, _, _) -> Console.WriteLine(msg)
