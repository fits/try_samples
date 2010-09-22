using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Routing;

namespace Fits.Sample.Web.Routing
{
    public static class CustomRouting
    {
        //Get メソッドを HttpApplication の拡張メソッドとして定義
        public static void Get(this HttpApplication app, string pattern, Func<RequestContext, string> proc)
        {
            Action("GET", pattern, proc);
        }

        //Post メソッドを HttpApplication の拡張メソッドとして定義
        public static void Post(this HttpApplication app, string pattern, Func<RequestContext, string> proc)
        {
            Action("POST", pattern, proc);
        }

        public static void Action(string methodType, string pattern, Func<RequestContext, string> proc)
        {
            RouteTable.Routes.Add(new Route(pattern, new CustomRouteHandler(proc))
            {
                //HTTP Method による制約を指定
                Constraints = new RouteValueDictionary{{"httpMethod", new HttpMethodConstraint(methodType)}}
            });
        }


        private class CustomRouteHandler : IRouteHandler
        {
            private Func<RequestContext, string> proc;

            public CustomRouteHandler(Func<RequestContext, string> proc)
            {
                this.proc = proc;
            }

            public IHttpHandler GetHttpHandler(RequestContext requestContext)
            {
                return new CustomHttpHandler(this.proc, requestContext);
            }
        }

        private class CustomHttpHandler : IHttpHandler
        {
            private Func<RequestContext, string> proc;
            private RequestContext reqCtx;

            public CustomHttpHandler(Func<RequestContext, string> proc, RequestContext reqCtx)
            {
                this.proc = proc;
                this.reqCtx = reqCtx;
            }

            public bool IsReusable
            {
                get { return true; }
            }

            public void ProcessRequest(HttpContext context)
            {
                //proc の処理を実行
                string res = this.proc(this.reqCtx);

                if (res != null)
                {
                    //proc の処理結果を HTTP レスポンスに出力
                    context.Response.Write(res);
                }
            }
        }

    }
}
