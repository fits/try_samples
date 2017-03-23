
import akka.actor.AbstractActor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val system = ActorSystem.apply("sample1")
    system.actorOf(Props.create(SampleListener::class.java))

    readLine()

    Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
}

class SampleListener : AbstractActor() {
    private val getCluster: (ActorSystem) -> Cluster = Cluster::get
    private val cluster = getCluster(context.system)

    /**
     * 以下のように Cluster.get(context.system) とするとコンパイルエラーが発生
     *
     * ・・・Overload resolution ambiguity:
     * public open fun get(p0: ActorSystem!): Extension! defined in akka.cluster.Cluster
     * public open fun get(p0: ActorSystem!): Cluster! defined in akka.cluster.Cluster
     */
    // private val cluster = Cluster.get(context.system)

    override fun createReceive(): Receive = receiveBuilder()
            .matchAny { msg ->
                println("<${self}> receive : ${msg}")
            }
            .build()

    override fun preStart() {
        println("<${self}> preStart")

        super.preStart()

        cluster.subscribe(
                self,
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent::class.java,
                ClusterEvent.UnreachableMember::class.java)
    }

    override fun postStop() {
        println("<${self}> postStop")

        super.postStop()

        cluster.leave(cluster.selfAddress())
        cluster.unsubscribe(self)
    }
}
