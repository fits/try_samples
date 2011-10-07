
open System
open System.IO
open System.Net

[<EntryPoint>]
let main(args: string[]) = 
    let downloadFile (dir: string) (url: string) = 
        async {
            let req = WebRequest.Create(url)
            let! res = req.AsyncGetResponse()

            let fileName = Path.Combine(dir, Path.GetFileName(url))

            use stream = res.GetResponseStream()
            use fs = new FileStream(fileName, FileMode.Create)

            let clen = int res.ContentLength

            let! buf = stream.AsyncRead(clen)
            do! fs.AsyncWrite(buf, 0, buf.Length)

            stdout.WriteLine("success: {0}", url)
        }

    downloadFile args.[0] args.[1] |> Async.RunSynchronously

    0
