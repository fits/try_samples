
let size xs = xs |> Seq.length |> float

let gini1 xs = xs |> Seq.countBy id |> Seq.sumBy (fun (k, v) -> (float v / size xs) ** 2.0) |> (-) 1.0

let list = ["A"; "B"; "B"; "C"; "B"; "A";]

printfn "%A" list
printfn "%A" (gini1 list)
