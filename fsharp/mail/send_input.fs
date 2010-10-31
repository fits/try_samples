open System
open System.Net.Mail

[<EntryPoint>]
let main(args: string[]) =
    if args.Length <> 4 then
        failwith "引数が必要 <SMTPサーバー> <From> <To> <Subject>"

    try
        let smtp = new SmtpClient(args.[0])
        //標準入力を読み込み
        let body = Console.In.ReadToEnd()

        smtp.Send(args.[1], args.[2], args.[3], body)
    with
    | _ as ex -> failwith ex.Message

    0
