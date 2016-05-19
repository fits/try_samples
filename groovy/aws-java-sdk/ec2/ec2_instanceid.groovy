@Grab('com.amazonaws:aws-java-sdk:1.11.1')
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.model.Filter

def name = args[0]

def cred = new PropertiesCredentials(new File('config.properties'))

def ec2 = new AmazonEC2Client(cred)

try {
	ec2.region = Region.getRegion(Regions.AP_NORTHEAST_1)

	def req = new DescribeInstancesRequest(
		filters: [ new Filter(name: 'tag:Name', values: [ name ]) ]
	)

	def res = ec2.describeInstances(req)

	println res.reservations*.instances*.instanceId.flatten().head()

} finally {
	ec2.shutdown()
}