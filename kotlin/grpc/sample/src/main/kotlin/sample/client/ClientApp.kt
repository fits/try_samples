
package sample.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit

import sample.*

fun main(args: Array<String>) {
    val port = 50051
    val channel = ManagedChannelBuilder.forAddress("localhost", port)
                                        .usePlaintext()
                                        .build()

    val stub = SampleServiceGrpc.newFutureStub(channel)

    val msg = SampleRequest.newBuilder().setMessage(args[0]).build()

    val future = stub.call(msg)

    val res = future.get(5, TimeUnit.SECONDS)

    println(res)

    channel.shutdown().awaitTermination(1, TimeUnit.SECONDS)
}
