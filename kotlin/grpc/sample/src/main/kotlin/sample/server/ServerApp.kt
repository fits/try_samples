
package sample.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver

import sample.*

fun main() {
    val port = 50051

    val server = ServerBuilder.forPort(port)
                                .addService(ServerImpl())
                                .build().start()

    println("server started ...")

    Runtime.getRuntime().addShutdownHook(Thread {
        println("server stop")
        server.shutdown()
    })

    server.awaitTermination()
}

internal class ServerImpl : SampleServiceGrpc.SampleServiceImplBase() {
    override fun call(req: SampleRequest, 
                        obs: StreamObserver<SampleResponse>) {

        val msg = SampleResponse.newBuilder()
                                .setMessage("${req.getMessage()}!!!")
                                .build()

        obs.onNext(msg)
        obs.onCompleted()
    }
}
