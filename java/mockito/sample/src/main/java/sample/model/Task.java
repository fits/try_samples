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

	public String getTitle() {
		return title;
	}
}
