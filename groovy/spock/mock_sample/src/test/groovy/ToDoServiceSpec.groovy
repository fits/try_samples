package fits.sample.service

import spock.lang.*

import fits.sample.dao.*
import fits.sample.model.*

class ToDoServiceSpec extends Specification {
	def service
	TaskDao mockDao

	def setup() {
		service = new ToDoService()
		mockDao = Mock()

		ToDoService.metaClass.setAttribute(service, "dao", mockDao)
	}

	def "タスクの追加"() {
		when:
			service.addTask("test")

		then:
			mockDao.addTask("test") >> new Task(taskId: 1, title: "test")
	}

	def "登録済みタスクの取得"() {
		when:
			String res = service.getTaskTitle(1)

		then:
			mockDao.getTask(1) >> new Task(taskId: 1, title: "test")
			res == "test"
	}

	def "未登録タスクの取得"() {
		when:
			String res = service.getTaskTitle(2)

		then:
			mockDao.getTask(2) >> null
			res == ""
	}
}

