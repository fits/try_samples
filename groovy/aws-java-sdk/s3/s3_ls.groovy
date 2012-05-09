@Grab('com.amazonaws:aws-java-sdk:1.3.8')
import com.amazonaws.services.s3.*
import com.amazonaws.auth.*

def s3 = new AmazonS3Client(new BasicAWSCredentials(args[0], args[1]))

s3.listBuckets().each {
	println it
}
