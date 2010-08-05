let plus x y = x + y
printfn "10 + 15 = %i" (plus 10 15)

let apply (f: int -> int -> int) x y = f x y
//ƒ‰ƒ€ƒ_®
printfn "100 * 2 = %i" (apply (fun x y -> x * y) 100 2)

//Ä‹AŠÖ”
let rec fact n = if n = 1 then 1 else n * fact(n - 1)
printfn "5! = %i" (fact 5)


