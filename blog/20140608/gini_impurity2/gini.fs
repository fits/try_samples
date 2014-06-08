
let size xs = xs |> Seq.length |> float

// (a) 1 - (AA + BB + CC)
let giniA xs = xs |> Seq.countBy id |> Seq.sumBy (fun (k, v) -> (float v / size xs) ** 2.0) |> (-) 1.0

let combinationCount cs = [
    for x in cs do
        for y in cs do
            if fst x <> fst y then
                yield (snd x, snd y)
]

// (b) AB + AC + BA + BC + CA + CB
let giniB xs = xs |> Seq.countBy id |> combinationCount |> Seq.sumBy (fun (x, y) -> (float x / size xs) * (float y / size xs))

let list = ["A"; "B"; "B"; "C"; "B"; "A";]

printfn "%A" (giniA list)
printfn "%A" (giniB list)
