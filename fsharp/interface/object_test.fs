
type ISample = 
    abstract member Print : unit -> unit

type SampleObj(x: int) = 
    interface ISample with
        member this.Print() = printfn "x = %d" x

let s1 = new SampleObj(10)
(s1 :> ISample).Print()


