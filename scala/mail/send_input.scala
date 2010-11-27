
import scala.io.Source
import org.apache.commons.mail.SimpleEmail

if (args.length != 4) {
	println("引数： <SMTPサーバー> <From> <To> <Subject>")
	exit
}

new SimpleEmail {
	charset = "UTF-8"
	hostName = args(0)
	setFrom(args(1))
	addTo(args(2))
	setSubject(args(3))
	setMsg(Source.stdin.mkString.replaceAll("\t", "　　"))
}.send

