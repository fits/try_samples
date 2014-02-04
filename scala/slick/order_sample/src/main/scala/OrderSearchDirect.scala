
import scala.slick.driver.PostgresDriver
import PostgresDriver.simple.Database
import Database.{ dynamicSession => session }
import scala.slick.direct._
import scala.slick.direct.AnnotationMapper._

// 今のところ BigDecimal や Timestamp をサポートしていないので使えない
@table(name = "orders")
case class COrders(
	@column(name = "order_id")
	orderId: Int,
	@column(name = "customer_id")
	customerId: Int,
	@column(name = "subtotal")
	subtotal: Double,
	@column(name = "discount")
	discount: Double,
	@column(name = "total")
	total: Double,
	@column(name = "create_date")
	createDate: String,
	@column(name = "update_date")
	updateDate: String
)

object OrderSearchDirect extends App {
	override def main(args: Array[String]): Unit = {
		val db = Database.forURL("jdbc:postgresql://localhost/sampledb", "user1", "")

		val q = Queryable[COrders]

		db.withDynSession {
			val backend = new SlickBackend(PostgresDriver, AnnotationMapper)

			backend.result(q.filter( _.customerId == 1), session).foreach(println)
		}
	}
}
