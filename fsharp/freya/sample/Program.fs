module Program

open KestrelInterop

[<EntryPoint>]
let main argv =
    let configureApp =
        ApplicationBuilder.useFreya Api.root

    WebHost.create ()
    |> WebHost.bindTo [|"http://0.0.0.0:5000"|]
    |> WebHost.configure configureApp
    |> WebHost.buildAndRun

    0
