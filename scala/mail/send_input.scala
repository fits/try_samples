
import scala.io.Source
import org.apache.commons.mail.SimpleEmail

if (args.length != 4) {
	println("引数： <SMTPサーバー> <From> <To> <Subject>")
	exit
}

val body = Source.stdin.mkString

val smtp = new SimpleEmail
smtp.setCharset("UTF-8")
smtp.setHostName(args(0))
smtp.setFrom(args(1))
smtp.addTo(args(2))
smtp.setSubject(args(3))
smtp.setMsg(body).send
