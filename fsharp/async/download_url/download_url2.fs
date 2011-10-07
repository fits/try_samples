
open System
open System.IO
open System.Net

[<EntryPoint>]
let main(args: string[]) = 
    let downloadFile (dir: string) (url: string) = 
        async {
            try
                let req = WebRequest.Create(url)
                let! res = req.AsyncGetResponse()

                let fileName = Path.Combine(dir, Path.GetFileName(url))

                use stream = res.GetResponseStream()
                use fs = new FileStream(fileName, FileMode.Create)

                let! buf = stream.AsyncRead(int res.ContentLength)
                do! fs.AsyncWrite(buf, 0, buf.Length)

                stdout.WriteLine("success: {0}", url)
            with
            | _ as e -> stdout.WriteLine("failed: {0}, {1}", url, e)
        }

    downloadFile args.[0] args.[1] |> Async.RunSynchronously

    0
