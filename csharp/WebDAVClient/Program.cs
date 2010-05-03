using System;
using System.Collections.Generic;
using System.Net;
using System.Text;

namespace WebDAVClient
{
    class Program
    {
        static void Main(string[] args)
        {
            WebClient wc = new WebClient();
            wc.BaseAddress = "http://localhost/webdav/";

			CredentialCache ccache = new CredentialCache();
			//Basic認証を使う場合の設定
			ccache.Add(new Uri(wc.BaseAddress), "Basic", new NetworkCredential("tester", "testerpass"));
			//Digest認証を使う場合の設定
			ccache.Add(new Uri(wc.BaseAddress), "Digest", new NetworkCredential("tester", "testerpass"));

			wc.Credentials = ccache;

            wc.UploadString("test1", "MKCOL", "");

            wc.UploadFile("test1/data1.txt", "PUT", "../../data1.txt");
        }
    }
}
