namespace Fits.Sample

open System
open System.Web
open System.Web.UI
open System.Web.UI.WebControls

type DefaultPage() =
    inherit Page()

    [<DefaultValue>]
    val mutable Label1 : Label

    member this.Page_Load(sender : obj, e : EventArgs) =
        this.Label1.Text <- "Hello World!"
