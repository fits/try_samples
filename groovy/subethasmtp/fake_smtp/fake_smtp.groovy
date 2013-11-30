@Grab('org.subethamail:subethasmtp:3.1.7')
@Grab('org.slf4j:slf4j-nop:1.7.5')
import org.subethamail.smtp.server.SMTPServer
import org.subethamail.smtp.helper.SimpleMessageListener
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

def server = new SMTPServer(new SimpleMessageListenerAdapter([
	accept: { from, recipient ->
		true
	},
	deliver: { from, recipient, data -> 
		println "from: ${from}, to: ${recipient}"

		def msg = new MimeMessage(Session.getInstance(new Properties()), data)

		if (msg.content instanceof MimeMultipart) {
			(0..<msg.content.count).each {
				def body = msg.content.getBodyPart(it)
				def enc = body.contentType.split('charset=').last()

				println body.inputStream.getText(enc)
			}
		}
		else {
			println msg.content
		}
	}
] as SimpleMessageListener))

server.start()

println "smtp started ..."

System.in.read()

server.stop()
