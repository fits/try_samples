package fits.sample;

import java.util.Date;

public class Comment {
	private String content;
	private Date createdDate;

	public Comment() {
		this.createdDate = new Date();
	}

	public String getContent() {
		return this.content;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}
}
