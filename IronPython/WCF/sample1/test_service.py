
import clr
clr.AddReference("System.ServiceModel")

from System import *
from System.ServiceModel import *

#@ServiceBehaviorAttribute(InstanceContextMode = InstanceContextMode.Single)
class TestService:
	_clrclassattribs = [ServiceContractAttribute()]

	#@OperationContractAttribute()
	def hello(msg):
		return "called hello: %s" % msg

