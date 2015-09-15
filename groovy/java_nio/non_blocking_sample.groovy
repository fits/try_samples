
import static java.nio.charset.StandardCharsets.*

import java.net.InetSocketAddress

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.spi.SelectorProvider

def provider = SelectorProvider.provider()
def selector = provider.openSelector()

def serverChannel = provider.openServerSocketChannel()

serverChannel.configureBlocking(false)
serverChannel.bind(new InetSocketAddress(8083))

serverChannel.register(selector, SelectionKey.OP_ACCEPT)

println serverChannel.isBlocking()
println serverChannel.isRegistered()

def accept = { channel, keySelector ->
	println "*** accept: ${channel}"

	def socketChannel = channel.accept()
	socketChannel.configureBlocking(false)

	socketChannel.register(keySelector, SelectionKey.OP_READ)
}

def read = { channel ->
	println "*** read: ${channel}"

	def buffer = ByteBuffer.allocate(10240)

	channel.read(buffer)

	buffer.flip()

	println buffer

	channel.write(UTF_8.encode(CharBuffer.wrap('ok')))

	channel.close()
}

// 10秒間接続が無ければ終了
while (selector.select(10000) > 0) {
	println '**** select'

	def keys = selector.selectedKeys()

	keys.each { key ->
		keys.remove key

		if (key.isAcceptable()) {
			accept(key.channel(), key.selector())
		}
		else if (key.isReadable()) {
			read(key.channel())
		}
	}
}

serverChannel.close()
