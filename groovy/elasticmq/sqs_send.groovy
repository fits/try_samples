@Grab('com.amazonaws:aws-java-sdk:1.11.45')
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.auth.BasicAWSCredentials

def queueName = args[0]
def msg = args[1]

def client = new AmazonSQSClient(new BasicAWSCredentials("", ""))
client.setEndpoint("http://localhost:9324")

def getQueueUrl = { name -> 
	try {
		client.getQueueUrl(name).queueUrl
	} catch (e) {
		def res = client.createQueue(name)
		println "create queue: ${res}"

		client.getQueueUrl(name).queueUrl
	}
}

def res = client.sendMessage(getQueueUrl(queueName), msg)

println res

client.shutdown()
