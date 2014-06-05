
let size xs = xs |> Seq.length |> float

let gini1 xs = xs |> Seq.countBy id |> Seq.sumBy (fun (k, v) -> (float v / size xs) ** 2.0) |> (-) 1.0

let combinationCount cs = [
    for x in cs do
        for y in cs do
            if fst x <> fst y then
                yield (snd x, snd y)
]

let gini2 xs = xs |> Seq.countBy id |> combinationCount |> Seq.sumBy (fun (x, y) -> (float x / size xs) * (float y / size xs))

let combination cs = Seq.last cs :: Seq.toList cs |> Seq.pairwise

let gini2a xs = xs |> Seq.countBy id |> combination |> Seq.sumBy (fun ((_, x), (_, y)) -> (float x / size xs) * (float y / size xs) * 2.0)

let list = ["A"; "B"; "B"; "C"; "B"; "A";]

printfn "%A" list
printfn "%A" (gini1 list)
printfn "%A" (gini2 list)
printfn "%A" (gini2a list)

