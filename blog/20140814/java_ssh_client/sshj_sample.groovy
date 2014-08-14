@Grab('net.schmizz:sshj:0.10.0')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.transport.verification.PromiscuousVerifier

def client = new SSHClient()

// accept UnknownHostKey
client.addHostKeyVerifier(new PromiscuousVerifier())

client.connect('127.0.0.1', 2222)

client.authPublickey('vagrant', client.loadKeys('insecure_private_key'))

def session = client.startSession()

def cmd = session.exec('ls -al')

println IOUtils.readFully(cmd.inputStream)

session.close()
client.disconnect()
