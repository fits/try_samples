
import java.sql.Timestamp
import scala.math.BigDecimal
import scala.slick.driver.PostgresDriver.simple._

class Orders(tag: Tag) extends Table[(Int, Int, BigDecimal, BigDecimal, BigDecimal, Timestamp, Timestamp)](tag, "orders") {

	def orderId = column[Int]("order_id")
	def customerId = column[Int]("customer_id")
	def subtotal = column[BigDecimal]("subtotal")
	def discount = column[BigDecimal]("discount")
	def total = column[BigDecimal]("total")
	def createDate = column[Timestamp]("create_date")
	def updateDate = column[Timestamp]("update_date")

	def * = (orderId, customerId, subtotal, discount, total, createDate, updateDate)
}
