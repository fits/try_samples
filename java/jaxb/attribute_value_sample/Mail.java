import javax.xml.bind.annotation.*;

public class Mail {
	//XML 属性として出力
	@XmlAttribute
	public String type;
	//XML 要素への値として出力
	@XmlValue
	public String mail;

	public Mail() {
	}
	public Mail(String type, String mail) {
		this.type = type;
		this.mail = mail;
	}
}