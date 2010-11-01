import scala.io.Source
import org.apache.commons.mail.SimpleEmail

new SimpleEmail {
	charset = "UTF-8"
	hostName = args(0)
	setFrom(args(1))
	addTo(args(2))
	setSubject(args(3))
	//標準入力を文字列化して本文に設定
	setMsg(Source.stdin.mkString)
}.send

