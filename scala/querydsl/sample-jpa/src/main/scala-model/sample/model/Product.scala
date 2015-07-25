package sample.model

import javax.persistence.Entity
import javax.persistence.Id

import java.math.BigDecimal

@Entity
class Product {
	@Id
	var id: Long = _
	var name: String = _
	var price: BigDecimal = _
}
