@Grab('com.amazonaws:aws-java-sdk:1.11.1')
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient
import com.amazonaws.services.cloudwatch.model.Dimension
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest

def instanceId = args[0]

def cred = new PropertiesCredentials(new File('config.properties'))

def cw = new AmazonCloudWatchClient(cred)

try {
	cw.region = Region.getRegion(Regions.AP_NORTHEAST_1)

	def now = new Date()

	def req = new GetMetricStatisticsRequest(
		namespace: 'AWS/EC2',
		metricName: 'CPUUtilization',
		startTime: now.minus(1),
		endTime: now,
		period: 300,
		statistics: ['Average'],
		dimensions: [ new Dimension(name: 'InstanceId', value: instanceId) ]
	)

	def res = cw.getMetricStatistics(req)

	res.datapoints.sort {
		it.timestamp
	}.each {
		println([
			it.timestamp.format('yyyy-MM-dd HH:mm:ss'),
			it.average
		].join('\t'))
	}

} finally {
	cw.shutdown()
}