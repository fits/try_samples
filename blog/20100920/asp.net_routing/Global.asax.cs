using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Security;
using System.Web.SessionState;

using Fits.Sample.Web.Routing;

namespace Fits.Sample.Web
{
    public class Global : System.Web.HttpApplication
    {
        protected void Application_Start(object sender, EventArgs e)
        {
            CustomRouting.Post("test/page", ctx =>
            {
                ctx.HttpContext.Response.Redirect("/WebForm1.aspx");
                return null;
            });

            CustomRouting.Get("{name}/{index}", ctx =>
            {
                var param = ctx.RouteData.Values;
                return string.Format("hello {0} - {1}", param["name"], param["index"]);
            });
        }

    }
}