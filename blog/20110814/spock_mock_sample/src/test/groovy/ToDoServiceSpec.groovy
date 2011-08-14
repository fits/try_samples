package fits.sample.service

import spock.lang.*

import fits.sample.dao.*
import fits.sample.model.*

class ToDoServiceSpec extends Specification {
	def service
	TaskDao mockDao

	def setup() {
		service = new ToDoService()

        //モックの定義
        //def mockDao で定義して mockDao = Mock(TaskDao) でも可
		mockDao = Mock()

		//dao フィールドの値をモック mockDao に置き換え
		ToDoService.metaClass.setAttribute(service, "dao", mockDao)
	}

	def "タスクの追加に成功すると true"() {
		when:
			def res = service.addTask("test")

		then:
			mockDao.addTask("test") >> new Task(taskId: 1, title: "test")
			res == true
	}

	def "タスクの追加に失敗すると false"() {
		when:
			def res = service.addTask(null)

		then:
			//モックで例外を throw
			mockDao.addTask(null) >> {throw new IllegalArgumentException()}
			res == false
	}


	def "登録済みタスクのタイトル取得"() {
		when:
			String res = service.getTaskTitle(1)

		then:
			mockDao.getTask(1) >> new Task(taskId: 1, title: "test")

			//Groovy 1.8.1 では以下を res.equals("test") としなければ
			//テスト失敗となる
			res == "test"
	}

	def "未登録タスクのタイトル取得は例外発生"() {
		when:
			String res = service.getTaskTitle(2)

		then:
			mockDao.getTask(2) >> null

            //クロージャを使って引数の制約を記載する事も可
            //mockDao.getTask({it == 2}) >> null
            //引数の値がどうでも良ければ以下でも可
            //mockDao.getTask(_) >> null

			//例外の発生を検証
			thrown(NoTaskException)
	}
}

