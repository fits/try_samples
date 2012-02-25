package fits.sample.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
//クラス定義に存在しない JSON データのプロパティを無視するように設定
@JsonIgnoreProperties(ignoreUnknown=true)
public class Comment {
	public String content;

	public Comment() {
	}

	public Comment(String content) {
		this.content = content;
	}
}