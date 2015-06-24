package sample.model;

public class Task {
	private String taskId;
	private String title;

	public Task(String taskId, String title) {
		this.taskId = taskId;
		this.title = title;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
