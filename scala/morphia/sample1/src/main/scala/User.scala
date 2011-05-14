package fits.sample

import com.google.code.morphia.annotations._
import org.bson.types.ObjectId

@Entity
class User(@Property var name: String) {
	def this() = this("")

	@Id
	var id: ObjectId = null
}
