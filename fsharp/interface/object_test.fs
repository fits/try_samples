
//インターフェース定義
type ISample = 
    abstract member Print : unit -> unit

//実装オブジェクト定義
type SampleObj(x: int) = 
    //インターフェースの実装
    interface ISample with
        member this.Print() = printfn "x = %d" x

//実装オブジェクト定義（アップキャスト不要にするためのメソッド定義版）
type SampleObj2(x: int) = 
    //アップキャストを不要にするために実装側でメソッドを用意
    member this.Print() = (this :> ISample).Print()
    //インターフェースの実装
    interface ISample with
        member this.Print() = printfn "x = %d" x


let s1 = new SampleObj(10)

//インターフェースメソッドを呼び出すには、
//インターフェース型にアップキャストして呼び出す必要あり
(s1 :> ISample).Print()

//オブジェクト側で定義したメソッド呼び出し
let s2 = new SampleObj2(76)
s2.Print()

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

