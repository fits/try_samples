
open System
open Chiron

[<EntryPoint>]
let main argv =
    let d = Map ["a1", 12]
    let res = Inference.Json.serialize d

    printfn "json = %s" res
    0
