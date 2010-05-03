
import clr
clr.AddReference("System.ServiceModel")
clr.AddReference("CommonLib.dll")

from System import *
from System.ServiceModel import *
from CommonLib import *

binding = NetNamedPipeBinding(NetNamedPipeSecurityMode.None)
address = EndpointAddress("net.pipe://localhost/test")

factory = ChannelFactory[IDownloadManager](binding, address)
dm = factory.CreateChannel()

ret = dm.CopyFile("aaaa")

print "received : %s" % ret

factory.Close()

