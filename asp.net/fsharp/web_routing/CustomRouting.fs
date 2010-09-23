namespace Fits.Sample.Web.Routing

open System
open System.Web
open System.Web.Routing

module CustomRouting = 
    type HttpApplication with
        member this.Get(pattern: string, proc: Func<RequestContext, string>) = "aaa"
        //this.Action("GET", pattern, proc)
