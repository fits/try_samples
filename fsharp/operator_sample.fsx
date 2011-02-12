
let (+-) x y = x + y + x - y

let a = 5 +- 3
printfn "%d" a

let b = (+-) 5 3
printfn "%d" b

