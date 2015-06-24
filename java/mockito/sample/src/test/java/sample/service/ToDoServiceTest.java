package sample.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import sample.dao.TaskDao;
import sample.model.Task;

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
	public void addTask() {
		service.addTask("sample");

		verify(dao).addTask("sample");
	}

	@Test
	public void getTaskTitle() {
		when(dao.getTask("id1")).thenReturn(new Task("id1", "sample"));

		String res = service.getTaskTitle("id1");

		assertThat(res, is("sample"));
	}

	@Test
	public void getTaskTitleNoTask() {
		String res = service.getTaskTitle("id2");

		assertThat(res, is(""));
	}
}
