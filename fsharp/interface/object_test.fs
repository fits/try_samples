
type ISample = 
    abstract member Print : unit -> unit

type SampleObj(x: int) = 
    interface ISample with
        member this.Print() = printfn "x = %d" x

let s1 = new SampleObj(10)

//インターフェースメソッドを呼び出すには、
//インターフェース型にアップキャストして呼び出す必要あり
(s1 :> ISample).Print()


//オブジェクト式を使ってメソッド呼び出し1
let createSample(x: int) =
    { new ISample with
        member this.Print() = printfn "create sample %d" x
    }

createSample(50).Print()


//オブジェクト式を使ってメソッド呼び出し2
{new ISample with
    member this.Print() = printfn "testdata"
}.Print()

