
open System.IO
open System.Net.Mail

[<EntryPoint>]
let main(args: string[]) =
    if args.Length <> 5 then
        failwith "引数が必要 <SMTPサーバー> <From> <To> <件名> <ファイル名>"

    try
        let smtp = new SmtpClient(args.[0])
        let body = File.ReadAllText(args.[4])

        smtp.Send(args.[1], args.[2], args.[3], body)
    with
    | _ as ex -> failwith ex.Message

    0
