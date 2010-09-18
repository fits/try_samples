using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.SessionState;
using System.Web.Routing;

namespace RoutingSample
{
    public class Global : System.Web.HttpApplication
    {

        protected void Application_Start(object sender, EventArgs e)
        {
            RouteTable.Routes.Add(new Route("{name}/{index}", new TestHandler()));

        }

        protected void Session_Start(object sender, EventArgs e)
        {

        }

        protected void Application_BeginRequest(object sender, EventArgs e)
        {

        }

        protected void Application_AuthenticateRequest(object sender, EventArgs e)
        {

        }

        protected void Application_Error(object sender, EventArgs e)
        {

        }

        protected void Session_End(object sender, EventArgs e)
        {

        }

        protected void Application_End(object sender, EventArgs e)
        {

        }

        private class TestHandler : IRouteHandler
        {
            public IHttpHandler GetHttpHandler(RequestContext requestContext)
            {
                string name = requestContext.RouteData.Values["name"] as string;
                string index = requestContext.RouteData.Values["index"] as string;

                return new TestHttpHandler(name, index);
            }
        }

        private class TestHttpHandler : IHttpHandler
        {
            private string name;
            private string index;

            public TestHttpHandler(string name, string index)
            {
                this.name = name;
                this.index = index;
            }

            public bool IsReusable
            {
                get { return true; }
            }

            public void ProcessRequest(HttpContext context)
            {
                context.Response.Write(string.Format("hello world : {0} - {1}", name, index));
            }
        }
    }
}