package sample.model

import javax.persistence._

@Entity
@Table(name = "product_variation")
class ProductVariation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = _
	var color: String = _
	var size: String = _

	override def toString = s"ProductVariation(id: ${id}, color: ${color}, size: ${size})"
}
