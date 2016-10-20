@Grab('com.amazonaws:aws-java-sdk:1.11.45')
import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.auth.BasicAWSCredentials

def queueName = args[0]

def client = new AmazonSQSClient(new BasicAWSCredentials("", ""))
client.setEndpoint("http://localhost:9324")

def queueUrl = client.getQueueUrl(queueName).queueUrl

def msgRes = client.receiveMessage(queueUrl)

msgRes.messages.each { msg ->
	println msg

	client.deleteMessage(queueUrl, msg.receiptHandle)
}

client.shutdown()
