@Grab('com.jcraft:jsch:0.1.51')
import com.jcraft.jsch.JSch

def jsch = new JSch()

jsch.addIdentity('insecure_private_key')
// accept UnknownHostKey
jsch.setConfig('StrictHostKeyChecking', 'no')

def session = jsch.getSession('vagrant', '127.0.0.1', 2222)

session.connect()

def ch = session.openChannel('exec')
ch.setCommand('ls -al')

ch.connect()

println ch.inputStream.text

ch.disconnect()

session.disconnect()
