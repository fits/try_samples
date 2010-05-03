
using System;
using System.ServiceModel;
using CommonLib;

public class TestServer
{
	static void Main(string[] args)
	{
		string uri = "net.pipe://localhost/test";

        DownloadManager dm = new DownloadManager();
        dm.CopyFileCallBack = delegate(string file)
        {
            return string.Format("get : {0}", file);
        };

		using (ServiceHost host = new ServiceHost(dm))
		{
            NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
            host.AddServiceEndpoint(typeof(IDownloadManager), binding, uri);

            host.Open();

			Console.WriteLine("press enter key to terminate");
			Console.ReadLine();

			host.Close();
		}
	}
}
