
using System;
using System.ServiceModel;
using CommonLib;

public class TestApp
{
	static void Main(string[] args)
	{
		NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
		EndpointAddress address = new EndpointAddress("net.pipe://localhost/test");
		using (ChannelFactory<IDownloadManager> factory = new ChannelFactory<IDownloadManager>(binding, address))
		{
		    IDownloadManager dm = factory.CreateChannel();

		    if (dm != null)
		    {
		        string msg = dm.CopyFile("test file");
		        Console.WriteLine(msg);
		    }

		    factory.Close();
		}
	}
}
