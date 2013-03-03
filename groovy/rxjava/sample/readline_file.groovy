@Grab('com.netflix.rxjava:rxjava-groovy:0.5.3')
import rx.*

def fromFile = { file ->
	Observable.create { observer ->
		try {
			new File(file).eachLine {
				observer.onNext(it)
			}
			observer.onCompleted()
		} catch (e) {
			observer.onError(e)
		}
	}
}

fromFile(args[0]).skip(1).take(2).map { "#${it}" } subscribe { println it }
