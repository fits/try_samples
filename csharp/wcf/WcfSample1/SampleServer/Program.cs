using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ServiceModel;

using TestServiceLib;

namespace SampleServer
{
    class Program
    {
        static void Main(string[] args)
        {
            string uri = "net.pipe://localhost/sample/test";

            using (ServiceHost host = new ServiceHost(new TestService()))
            {
                NetNamedPipeBinding binding = new NetNamedPipeBinding(NetNamedPipeSecurityMode.None);
                host.AddServiceEndpoint(typeof(ITestService), binding, uri);

                host.Open();

                Console.WriteLine("press <enter> key to terminate service");

                Console.ReadLine();

                host.Close();
            }
        }
    }
}
