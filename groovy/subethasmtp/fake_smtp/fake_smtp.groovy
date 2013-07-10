@Grab('org.subethamail:subethasmtp:3.1.7')
import org.subethamail.smtp.server.SMTPServer
import org.subethamail.smtp.helper.SimpleMessageListener
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter
import javax.mail.Session
import javax.mail.internet.MimeMessage

def server = new SMTPServer(new SimpleMessageListenerAdapter([
	accept: { from, recipient ->
		true
	},
	deliver: { from, recipient, data -> 
		println "from: ${from}, to: ${recipient}"

		def msg = new MimeMessage(Session.getInstance(new Properties()), data)
		println msg.content
	}
] as SimpleMessageListener))

server.start()

println "smtp started ..."

System.in.read()

server.stop()
