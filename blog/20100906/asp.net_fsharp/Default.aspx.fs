namespace Fits.Sample

open System
open System.Web
open System.Web.UI
open System.Web.UI.WebControls

type DefaultPage() =
    inherit Page()

    [<DefaultValue>] val mutable InfoText : TextBox
    [<DefaultValue>] val mutable InfoButton : Button
    [<DefaultValue>] val mutable InfoLabel : Label

    //ページロード時の処理
    member this.Page_Load(sender : obj, e : EventArgs) =
        this.InfoLabel.Text <- "hello"

    //ボタンクリック時の処理
    member this.InfoButton_Click(sender : obj, e : EventArgs) = 
        this.InfoLabel.Text <- "入力: " + this.InfoText.Text
