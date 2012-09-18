/*
 * インスタンスを実行するスクリプト
 *
 */
@Grab('com.amazonaws:aws-java-sdk:1.3.19')
import com.amazonaws.services.ec2.*
import com.amazonaws.services.ec2.model.*
import com.amazonaws.auth.*

def region = "ap-northeast-1"

def imageId = 'ami-8e3d8e8f' //Amazon Linux AMI インスタンスストア 32bit 東京
//def imageId = 'ami-943d8e95' //Amazon Linux AMI インスタンスストア 64bit 東京

def num = 5
def keyName = 'test-key'
def securityGroup = 'testGroup'
def instanceType = InstanceType.M1Small

def ec2 = new AmazonEC2Client(new PropertiesCredentials(new File("setting.properties")))

ec2.describeRegions(new DescribeRegionsRequest().withRegionNames(region)).regions.each {
	ec2.endpoint = it.endpoint

	def req = new RunInstancesRequest(
		imageId: imageId,
		instanceType: instanceType,
		keyName: keyName,
		securityGroups: [securityGroup],
		minCount: num,
		maxCount: num
	)

	def res = ec2.runInstances(req)

	res.reservation.instances.each {
		println it
	}
}
