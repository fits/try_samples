namespace Fits.Sample.Web

open System
open System.Web
open Fits.Sample.Web.Routing.CustomRouting

type Global() = 
    inherit HttpApplication()

    member this.Application_Start(sender: Object, e: EventArgs) = 
        this.Get("test", fun ctx -> "aaaa")

