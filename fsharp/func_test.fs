let apply (f: int -> int -> int) x y = f x y

let plus x y = x + y

printfn "10 + 15 = %i" (apply plus 10 15)

printfn "100 * 2 = %i" (apply (fun x y -> x * y) 100 2)
