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

def takeResponseCode = { it.split(',')[3] }
def isError = { it != '200' }

new File(args[0]).eachFileRecurse(FileType.FILES) { file ->

	fromFile(file).skip(1).map(takeResponseCode).filter(isError).count().subscribe {
		println "${file.name} : $it"
	}
}
