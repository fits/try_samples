package sample

import com.querydsl.jpa.impl.JPAQuery
import sample.model.Product
import sample.model.ProductVariation
import sample.model.QProduct

import javax.persistence.Persistence
import java.math.BigDecimal

import scala.collection.JavaConversions._

object SampleApp extends App{
	def product(name: String, price: BigDecimal, variationList: ProductVariation*) = {
		val res = new Product()

		res.name = name
		res.price = price

		variationList.foreach(res.variationList.add)

		res
	}

	def variation(color: String, size: String) = {
		val res = new ProductVariation()

		res.color = color
		res.size = size

		res
	}

	val emf = Persistence.createEntityManagerFactory("jpa")
	val em = emf.createEntityManager()

	val tx = em.getTransaction()
	tx.begin()

	val p1 = product(
		"sample" + System.currentTimeMillis(), 
		new BigDecimal(1250),
		variation("White", "L"),
		variation("Black", "M")
	)

	em.persist(p1)

	tx.commit()

	val p = QProduct as "p"

	val query = new JPAQuery[Product](em)

	val res = query.from(p).where(p.name.startsWith("sample")).fetch()

	res.foreach(println)

	em.close()

}
