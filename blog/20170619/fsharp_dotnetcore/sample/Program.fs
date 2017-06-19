
open System
open Chiron.Inference

[<EntryPoint>]
let main argv =
    let d = Map ["a", 123]
    let res = Json.serialize d

    printfn "json = %s" res
    0
