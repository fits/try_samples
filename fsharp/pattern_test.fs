
let printPattern n =
    match n with 
    | 1 | 2 -> printfn "1 or 2"
    | 3 -> printfn "three"
    | var1 -> printfn "%d" var1

List.iter printPattern [1 .. 10]

