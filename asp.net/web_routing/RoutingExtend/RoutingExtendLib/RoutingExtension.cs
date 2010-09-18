using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Routing;

namespace Fits.Routing
{
    public class RoutingExtension
    {
        public static void Get(string pattern, Func<RequestContext, string> proc)
        {
            Action("GET", pattern, proc);
        }

        public static void Post(string pattern, Func<RequestContext, string> proc)
        {
            Action("POST", pattern, proc);
        }

        public static void Action(string methodType, string pattern, Func<RequestContext, string> proc)
        {
            RouteTable.Routes.Add(new Route(pattern, new ExtensionRouteHandler(methodType, proc)));
        }

        /// <summary>
        /// 
        /// </summary>
        private class ExtensionRouteHandler : IRouteHandler
        {
            private string methodType;
            private Func<RequestContext, string> proc;

            public ExtensionRouteHandler(string methodType, Func<RequestContext, string> proc)
            {
                this.methodType = methodType;
                this.proc = proc;
            }

            public IHttpHandler GetHttpHandler(RequestContext requestContext)
            {
                if (requestContext.HttpContext.Request.HttpMethod == this.methodType)
                {
                    return new ExtensionHttpHandler(this.proc, requestContext);
                }
                else
                {
                    return new DefaultHttpHandler();
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        private class ExtensionHttpHandler : IHttpHandler
        {
            private Func<RequestContext, string> proc;
            private RequestContext reqCtx;

            public ExtensionHttpHandler(Func<RequestContext, string> proc, RequestContext reqCtx)
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
                string res = this.proc(this.reqCtx);

                if (res != null)
                {
                    context.Response.Write(res);
                }
            }
        }

    }
}
