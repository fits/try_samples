package fits.sample.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Book {
	public String id;
	public String title;

	public List<Comment> comments = new ArrayList<>();

	public Book() {
	}

	public Book(String id, String title) {
		this.id = id;
		this.title = title;
	}
}