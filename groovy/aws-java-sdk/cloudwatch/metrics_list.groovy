@Grab('com.amazonaws:aws-java-sdk:1.11.1')
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions

def cred = new PropertiesCredentials(new File('config.properties'))

def cw = new AmazonCloudWatchClient(cred)

try {
	cw.region = Region.getRegion(Regions.AP_NORTHEAST_1)

	def res = cw.listMetrics()

	res.metrics.each { println it }

} finally {
	cw.shutdown()
}