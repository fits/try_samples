package sample

import com.querydsl.jpa.impl.JPAQuery
import sample.model.Product
import sample.model.QProduct

import javax.persistence.Persistence
import java.math.BigDecimal

import scala.collection.JavaConversions._

object SampleApp extends App {
	val emf = Persistence.createEntityManagerFactory("jpa")
	val em = emf.createEntityManager()

	val tx = em.getTransaction()
	tx.begin()

	val p1 = new Product()
	p1.name = "test" + System.currentTimeMillis()
	p1.price = new BigDecimal(1250)

	em.persist(p1)

	tx.commit()

	val p = QProduct as "p"
	val query = new JPAQuery[Product](em)

	val res = query.from(p).where(p.name.startsWith("test")).fetch()

	res.foreach { r =>
		println(s"id: ${r.id}, name: ${r.name}, price: ${r.price}")
	}

	em.close()
}
