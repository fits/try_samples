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
            // "test/1" への post で WebForm1.aspx にリダイレクトする
            CustomRouting.Post("test/{index}", ctx =>
            {
                ctx.HttpContext.Response.Redirect("/WebForm1.aspx");
                return null;
            });

            // "test/1" への get で "hello test - 1" という文字列を表示
            CustomRouting.Get("{name}/{index}", ctx =>
            {
                var param = ctx.RouteData.Values;
                return string.Format("hello {0} - {1}", param["name"], param["index"]);
            });
        }

    }
}