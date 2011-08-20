package fits.sample.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "tasks")
public class Task {
	@Id
	@GeneratedValue
	@Column(name = "task_id")
	private long taskId;

	private String title;

	@Column(name = "created_date")
	private Date createdDate;

	public long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}

