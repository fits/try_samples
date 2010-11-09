
import clr
clr.AddReference("System.ServiceModel")

from System import *
from System.ServiceModel import *
from test_service import *


service = TestService()

host = ServiceHost(service)

binding = NetNamedPipeBinding(NetNamedPipeSecurityMode.None)

host.AddServiceEndpoint(TestService.GetType(), binding, "net.pipe://localhost/test")

host.Open()

Console.WriteLine("press enter key to terminate")
Console.ReadLine()

host.Close()

