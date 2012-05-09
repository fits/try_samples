@Grab('com.amazonaws:aws-java-sdk:1.3.8')
import com.amazonaws.services.s3.*
import com.amazonaws.auth.*
import groovyx.gpars.GParsExecutorsPool

def s3 = new AmazonS3Client(new BasicAWSCredentials(args[0], args[1]))

def flist = []

new File(args[2]).eachFileRecurse {f ->
	if (f.name.endsWith(".png")) {
		flist.add f
	}
}

GParsExecutorsPool.withPool(10) {
	flist.eachParallel {f ->
		def key = f.path.replace("\\", "/")

		try {
			def pres = s3.putObject(args[3], key, f)
			println pres
		} catch (e) {
			println e
		}
	}
}

