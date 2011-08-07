package fits.sample.model;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "tasks")
public class Task {
	@Id
	@GeneratedValue
	@Column(name = "task_id")
	private BigInteger taskId;
	private String title;
	private Timestamp created;
	private Timestamp modified;

	public BigInteger getTaskId() {
		return taskId;
	}

	public void setTaskId(BigInteger taskId) {
		this.taskId = taskId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
}

