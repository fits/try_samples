@Grab('com.amazonaws:aws-java-sdk:1.3.8')
import com.amazonaws.services.s3.*
import com.amazonaws.auth.*

def s3 = new AmazonS3Client(new BasicAWSCredentials(args[0], args[1]))

s3.listObjects(args[2], args[3]).each {
	println "*** bucketName = ${it.bucketName}, prefix = ${it.prefix}"

	it.objectSummaries.each {s ->
		println "key = ${s.key}, size = ${s.size}, bucketName = ${s.bucketName}"
	}
}
