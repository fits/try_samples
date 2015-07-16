package sample.model

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Product {
	@Id
	var id: Long = _
	var name: String = _
}
