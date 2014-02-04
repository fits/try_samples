
import scala.slick.driver.PostgresDriver.simple._

object OrderSearch extends App {
	override def main(args: Array[String]): Unit = {
		val db = Database.forURL("jdbc:postgresql://localhost/sampledb", "user1", "")

		val orders = TableQuery[Orders]

		db.withSession { implicit session =>
			orders.filter( _.customerId is 1 ) foreach(println)
		}
	}
}
