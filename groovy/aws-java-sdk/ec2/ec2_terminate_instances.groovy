/*
 * 全インスタンスを終了するスクリプト
 *
 */
@Grab('com.amazonaws:aws-java-sdk:1.3.19')
import com.amazonaws.services.ec2.*
import com.amazonaws.services.ec2.model.*
import com.amazonaws.auth.*

def region = "ap-northeast-1"

def ec2 = new AmazonEC2Client(new PropertiesCredentials(new File("setting.properties")))

ec2.describeRegions(new DescribeRegionsRequest().withRegionNames(region)).regions.each {
	ec2.endpoint = it.endpoint

	def idList = ec2.describeInstances().reservations.collectMany {res ->
		res.instances.collect { it.instanceId }
	}

	def res = ec2.terminateInstances(new TerminateInstancesRequest(idList))

	res.terminatingInstances.each {
		println it
	}
}
