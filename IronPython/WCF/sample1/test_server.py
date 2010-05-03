
import clr
clr.AddReference("System.ServiceModel")
clr.AddReference("CommonLib.dll")

from System import *
from System.ServiceModel import *
from CommonLib import *

def copyfile(file):
	print "called copyfile\n"
	return "returned : %s" % file

service = DownloadManager()
#service.CopyFileCallBack = CopyFileDelegate(copyfile)
service.CopyFileCallBack = copyfile

host = ServiceHost(service)

binding = NetNamedPipeBinding(NetNamedPipeSecurityMode.None)

host.AddServiceEndpoint(IDownloadManager, binding, "net.pipe://localhost/test")

host.Open()

Console.WriteLine("press enter key to terminate")
Console.ReadLine()

host.Close()

