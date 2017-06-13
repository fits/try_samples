
open System
open Chiron.Inference

[<EntryPoint>]
let main argv =
    let d = Map ["a1", 12]
    let res = Json.serialize d

    printfn "json = %s" res
    0
