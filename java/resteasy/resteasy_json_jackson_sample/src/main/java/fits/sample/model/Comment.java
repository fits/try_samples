package fits.sample.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Comment {
	public String content;

	public Comment() {
	}

	public Comment(String content) {
		this.content = content;
	}
}