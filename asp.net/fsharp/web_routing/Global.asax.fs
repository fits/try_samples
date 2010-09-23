namespace Fits.Sample.Web

open System
open System.Web
open Fits.Sample.Web.Routing.CustomRouting

type Global() = 
    inherit HttpApplication()

    member this.Application_Start(sender: Object, e: EventArgs) = 

        this.Post("test/{index}", fun ctx -> 
            ctx.HttpContext.Response.Redirect("/WebForm1.aspx")
            null
        )

        this.Get("{name}/{index}", fun ctx -> 
            let pm = ctx.RouteData.Values
            sprintf "hello %O - %O" pm.["name"] pm.["index"]
        )

