
let (+-) x y = x + y + x - y

let a = 5 +- 3
printfn "%d" a

let b = (+-) 5 3
printfn "%d" b

// (++) だと ++ 10 のように使えないので ! を先頭に付ける
let (!++) x = x + 1

let c = !++ 10
printfn "%d" c
