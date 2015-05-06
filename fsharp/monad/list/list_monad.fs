
type ListMonad() =
    member this.Bind(x, f) = x |> List.map f |> List.concat
    member this.Return(x) = [x]

let list = new ListMonad()

let res = list {
    let! x = [1; 2; 3]
    let! y = ["a"; "b"]

    return (x, y)
}

printfn "result = %A" res