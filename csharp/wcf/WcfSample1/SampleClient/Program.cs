using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;

using TestServiceLib;

namespace SampleClient
{
    class Program
    {
        static void Main(string[] args)
        {
            NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
            EndpointAddress address = new EndpointAddress("net.pipe://localhost/sample/test");
            ChannelFactory<ITestService> ch = new ChannelFactory<ITestService>(binding, address);

            ITestService proxy = ch.CreateChannel();

            Console.WriteLine(proxy.GetData("abc"));

            ch.Close();
        }
    }
}
