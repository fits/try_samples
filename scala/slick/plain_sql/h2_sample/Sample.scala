package fits.sample

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}

object Sample extends App {

	case class User(name: String, address: String)

	Database.forURL("jdbc:h2:mem:sample1", driver = "org.h2.Driver") withSession {
		Q.updateNA("""
			create table users(
				name varchar not null,
				address varchar not null
			)
		""").execute

		(Q.u + "insert into users values ('aaa1', 'aaa')").execute

		val insertUser = Q[(String, String), Int] + """
			insert into users values(?, ?)
		"""

		insertUser("test1", "addr1").execute
		insertUser("test2", "addr2").execute
		insertUser("sample3", "addr3").execute

		implicit val getUserResult = GetResult(r => User(r.<<, r.<<))

		Q.queryNA[User]("select * from users") foreach { c =>
			println(s"name = ${c.name}, address = ${c.address}")
		}

		println("-----")

		import Q.interpolation

		def findUsersByName(name: String) = sql"""
			select * from users where name like $name
		""".as[User]

		findUsersByName("test%") foreach { c =>
			println(s"name = ${c.name}, address = ${c.address}")
		}
	}

}
