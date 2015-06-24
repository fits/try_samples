package sample.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;
import sample.dao.TaskDao;
import sample.model.Task;

import java.util.Optional;

public class ToDoServiceTest {
	private ToDoService service;
	private TaskDao dao;

	@Before
	public void setUp() {
		service = new ToDoService();
		dao = mock(TaskDao.class);
		Whitebox.setInternalState(service, "taskDao", dao);
	}

	@Test
	public void getTaskTitle() {
		when(dao.getTask("id1")).thenReturn(Optional.of(new Task("id1", "sample")));

		String res = service.getTaskTitle("id1");

		assertThat(res, is("sample"));
	}

	@Test
	public void getTaskTitleNoTask() {
		when(dao.getTask("id2")).thenReturn(Optional.empty());

		String res = service.getTaskTitle("id2");

		assertThat(res, is(""));
	}

	@Test
	public void addTask() {
		service.addTask("sample");

		verify(dao).setTask(any());
	}

	@Test
	public void addTask2() {
		service.addTask("sample");

		ArgumentCaptor<Task> taskArg = ArgumentCaptor.forClass(Task.class);
		verify(dao).setTask(taskArg.capture());

		Task t = taskArg.getValue();

		assertThat(t.getTaskId(), notNullValue());
		assertThat(t.getTitle(), is("sample"));
	}

	@Test
	public void updateTask() {
		when(dao.getTask("id1")).thenReturn(Optional.of(new Task("id1", "sample")));

		service.updateTask("id1", "sample2");

		ArgumentCaptor<Task> taskArg = ArgumentCaptor.forClass(Task.class);
		verify(dao).setTask(taskArg.capture());

		Task t = taskArg.getValue();

		assertThat(t.getTaskId(), is("id1"));
		assertThat(t.getTitle(), is("sample2"));
	}

	@Test
	public void updateNoTask() {
		when(dao.getTask("id1")).thenReturn(Optional.empty());

		service.updateTask("id1", "sample2");

		verify(dao, never()).setTask(any());
	}
}
