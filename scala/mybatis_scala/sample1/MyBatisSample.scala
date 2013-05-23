package fits.sample

import java.util.Date
import org.mybatis.scala.config._
import org.mybatis.scala.mapping._

object MyBatisSample extends App {

	class Task {
		var task_id: Int = _
		var title: String = _
		var created: Date = _
		var modified: Date = _
	}

	val findAll = new SelectList[Task] {
		def xsql = "select * from tasks order by title"
	}

	val config = Configuration(new java.io.FileReader("mybatis.xml"))

	config += findAll

	val db = config.createPersistenceContext

	db.readOnly { implicit session =>
		findAll() foreach { t =>
			println(s"task id: ${t.task_id}, title: ${t.title}")
		}
	}
}
