package sample.controller

import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.net.URI
import java.util.concurrent.CopyOnWriteArrayList

class SampleHandler : WebSocketHandler {
    private val list = CopyOnWriteArrayList<FluxSink<WebSocketMessage>>()

    override fun handle(session: WebSocketSession?): Mono<Void> {
        println("*** websocket connect : $session")

        return session?.let { ses ->
            ses.send(Flux.create { sink -> applySink(ses, sink) })
        } ?: Mono.empty()
    }

    private fun applySink(session: WebSocketSession, sink: FluxSink<WebSocketMessage>) {
        val name = queryParam(session.handshakeInfo.uri, "name")

        list.add(sink)

        sink.onCancel {
            println("*** Cancel : $session")
            list.remove(sink)
        }.onDispose {
            println("*** Dispose: $session")
            list.remove(sink)
        }

        session.receive().subscribe { msg ->
            println("*** receive: $msg")

            val payload = msg.payloadAsText

            list.forEach {
                try {
                    it.next(session.textMessage("$name: $payload"))
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun queryParam(uri: URI, key: String) =
            UriComponentsBuilder.fromUri(uri).build().queryParams[key]?.first() ?: ""
}