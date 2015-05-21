@Grab('javax.mail:javax.mail-api:1.5.3')
@Grab('com.sun.mail:javax.mail:1.5.3')
import javax.mail.Session
import javax.mail.Message
import javax.mail.internet.MimeMessage

def session = Session.getInstance(new Properties())

def msg = new MimeMessage(session)

msg.setSubject('メール送信テスト', 'UTF-8')
msg.setFrom('aaa@bbb.com')
msg.setRecipients(Message.RecipientType.TO, 'xxx@yyyy.co.jp')

msg.setText('サンプル', 'UTF-8')

msg.writeTo(System.out)
