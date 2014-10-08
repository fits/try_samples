@Grab('com.netflix.rxjava:rxjava-groovy:0.20.5')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import rx.*
import groovy.io.FileType

def fromFile = { f ->
	Observable.create({ ob ->
		try {
			f.eachLine('UTF-8') {
				ob.onNext it
			}

			ob.onCompleted()
		} catch (e) {
			ob.onError e
		}
	} as Observable.OnSubscribe)
}

def findFiles = {
	def files = []

	new File(it).eachFileRecurse(FileType.FILES) {
		files << it
	}

	files
}

def takeResponseCode = { it.split(',')[3] }
def isError = { it != '200' }

def obs = findFiles(args[0]).inject( Observable.empty() ) { acc, file ->
	Observable.concat(acc, fromFile(file).skip(1))
}

obs.map(takeResponseCode).filter(isError).count().subscribe {
	println it
}
