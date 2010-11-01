open System
open System.Net.Mail

[<EntryPoint>]
let main(args: string[]) =
    let smtp = new SmtpClient(args.[0])
    //標準入力を文字列化
    let body = Console.In.ReadToEnd()
    //メール送信
    smtp.Send(args.[1], args.[2], args.[3], body)
    0
