package fits.sample;

import java.util.ArrayList;
import java.util.List;

public class Book {
	private String title;
	private List<Comment> comments;

	public Book() {
		this.comments = new ArrayList<Comment>();
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Comment> getComments() {
		return this.comments;
	}
}
