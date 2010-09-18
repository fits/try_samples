using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.SessionState;

using Fits.Routing;

namespace SampleWebApp
{
    public class Global : System.Web.HttpApplication
    {
        protected void Application_Start(object sender, EventArgs e)
        {
//            RoutingExtension.Get("test/page", ctx =>
            RoutingExtension.Post("test/page", ctx =>
            {
                ctx.HttpContext.Response.Redirect("/WebForm1.aspx");
                return null;
            });

            RoutingExtension.Get("{name}/{index}", ctx =>
            {
                var param = ctx.RouteData.Values;
                return string.Format("hello {0} - {1}", param["name"], param["index"]);
            });
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
    }
}