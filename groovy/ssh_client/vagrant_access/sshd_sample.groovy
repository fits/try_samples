@Grab('org.apache.sshd:sshd-core:0.12.0')
@Grab('org.apache.mina:mina-core:3.0.0-M2')
@Grab('org.bouncycastle:bcpkix-jdk15on:1.51')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import org.apache.sshd.ClientChannel
import org.apache.sshd.SshClient
import org.apache.sshd.common.keyprovider.FileKeyPairProvider

def client = SshClient.setUpDefaultClient()
client.start()

def session = client.connect('vagrant', '127.0.0.1', args[0] as int).await().getSession()

def keyProvider = new FileKeyPairProvider(['insecure_private_key'] as String[])
session.addPublicKeyIdentity(keyProvider.loadKeys().iterator().next())

def auth = session.auth()
// verify の実行が必要
auth.verify()

if (auth.isSuccess()) {
	def ch = session.createExecChannel('ls -al')
	def baos = new ByteArrayOutputStream()

	ch.setOut(baos)

	ch.open()
	ch.waitFor(ClientChannel.CLOSED, 0)
	ch.close(false)

	println baos.toString()
}

session.close(false)
client.stop()
