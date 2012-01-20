package fits.sample;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ErrorData {
	public String code;
	public String message;
}
