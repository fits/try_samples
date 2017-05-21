
open System

[<EntryPoint>]
let main argv =
    let plus x = x + 3
    let times x = x * 2

    let f = plus >> times
    printfn "%i" (f 4)

    let g = plus << times
    printfn "%i" (g 4)

    0
