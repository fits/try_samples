open System
open System.IO
open System.Text

type Station = {
    StationName: string
    PrefName: string
    StationGroupCode: string
}

let prefMap = File.ReadAllLines("m_pref.csv", Encoding.Default)
                |> Seq.skip 1
                |> Seq.map (fun l -> 
                        let items = l.Split(',')
                        (items.[0], items.[1])
                    )
                |> Map.ofSeq

let lines = File.ReadAllLines("m_station.csv", Encoding.Default)

let list = lines 
            |> Seq.skip 1 
            |> Seq.map (fun l -> l.Split(',')) 
            |> Seq.groupBy (fun s -> 
                    {
                        StationName = s.[9]
                        PrefName = Map.find s.[10] prefMap
                        StationGroupCode = s.[5]
                    }
                ) 
            |> List.ofSeq 
            |> List.sortWith (fun a b -> Seq.length(snd b) - Seq.length(snd a)) 
            |> Seq.take 10

for s in list do
    let st = fst s
    stdout.WriteLine("{0}駅 ({1}) : {2}", st.StationName, st.PrefName, Seq.length((snd s)))
