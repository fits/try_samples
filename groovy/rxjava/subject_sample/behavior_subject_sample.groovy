@Grab('io.reactivex:rxjava:1.0.0-rc.10')
import rx.subjects.*

def obs1 = { println "obs1: ${it}" }
def obs2 = { println "obs2: ${it}" }

def subject = BehaviorSubject.create("default")

subject.subscribe obs1

subject.onNext 'A'
subject.onNext 'B'

subject.subscribe obs2

subject.onNext 'C'

subject.onCompleted()
