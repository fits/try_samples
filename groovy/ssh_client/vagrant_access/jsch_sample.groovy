@Grab('com.jcraft:jsch:0.1.51')
import com.jcraft.jsch.JSch

def jsch = new JSch()
// accept UnknownHostKey
jsch.setConfig('StrictHostKeyChecking', 'no')

jsch.addIdentity('insecure_private_key')

def session = jsch.getSession('vagrant', '127.0.0.1', args[0] as int)

session.connect()

def ch = session.openChannel('exec')
ch.setCommand 'ls -al'

ch.connect()

println ch.inputStream.text

ch.disconnect()

session.disconnect()
