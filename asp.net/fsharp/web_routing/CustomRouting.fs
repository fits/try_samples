namespace Fits.Sample.Web.Routing

open System
open System.Web
open System.Web.Routing

module CustomRouting = 
    type HttpApplication with
        member this.Get(pattern: string, proc: Func<RequestContext, string>) =
            this.Action("GET", pattern, proc)

        member this.Post(pattern: string, proc: Func<RequestContext, string>) =
            this.Action("POST", pattern, proc)

        member this.Action(methodType: string, pattern: string, proc: Func<RequestContext, string>) =
            let r = new Route(pattern, {
                new IRouteHandler with
                    member this.GetHttpHandler(rctx: RequestContext) = {
                        new IHttpHandler with
                            member this.IsReusable = true
                            member this.ProcessRequest(hctx: HttpContext) = 
                                let res = proc.Invoke(rctx)
                                hctx.Response.Write(res)
                    }
            })

            let rv = RouteValueDictionary()
            rv.Add("httpMethod", new HttpMethodConstraint(methodType))
            r.Constraints <- rv

            RouteTable.Routes.Add(r)
