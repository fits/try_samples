package sample.model

import javax.persistence._

import java.util.ArrayList
import java.util.List
import java.math.BigDecimal

@Entity
class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = _
	var name: String = _
	var price: BigDecimal = _

	@OneToMany(fetch = FetchType.EAGER, cascade= Array(CascadeType.ALL))
	@JoinColumn(name = "product_id")
	val variationList: List[ProductVariation] = new ArrayList()

	override def toString = s"Product(id: ${id}, name: ${name}, price: ${price}, variationList: ${variationList})"
}
