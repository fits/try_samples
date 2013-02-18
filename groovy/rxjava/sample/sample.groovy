@Grab('com.netflix.rxjava:rxjava-groovy:0.5.3')
import rx.*

Observable.toObservable("a", "b", "c").subscribe { println it }

println "-----"

Observable.from("a", "b", "c", "d", "e").skip(1).take(2).subscribe { println it }

