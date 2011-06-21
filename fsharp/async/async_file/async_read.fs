
open System
open System.IO
open System.Text

[<EntryPoint>]
let main (args: string[]) =

    let readFile fileName = 
        async {
            use fs = new FileStream(fileName, FileMode.Open)

            let buf = Array.create (int fs.Length) 0uy

            let! asyncResult = fs.AsyncRead(buf, 0, buf.Length)

            return buf
        }

    let res = Async.RunSynchronously (readFile args.[0])

    Console.WriteLine("*** read completed ***")
    Console.WriteLine(Encoding.Default.GetString(res))

    0
