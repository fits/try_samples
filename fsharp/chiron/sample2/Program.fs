
open System
open Chiron
open Chiron.Inference

type Data =
    { Id : string
      Value : int }

    static member ToJson (x: Data) =
        Json.encode (Map.ofList [ ("id", x.Id); ("value", string x.Value) ])

[<EntryPoint>]
let main argv =
    let d = { Id = "a1"; Value = 123 }
    let res = Json.serialize d

    printfn "json = %s" res
    0
