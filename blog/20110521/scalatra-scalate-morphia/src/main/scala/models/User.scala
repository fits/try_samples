package fits.sample.models

import com.google.code.morphia.annotations._
import org.bson.types.ObjectId

@Entity(value = "users", noClassnameStored = true)
class User(var name: String) {

	def this() = this("")

	@Id var id: ObjectId = null
}
