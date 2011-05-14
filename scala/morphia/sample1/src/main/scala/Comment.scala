package fits.sample

import java.util.Date
import com.google.code.morphia.annotations._

class Comment(var content: String, var createdDate: Date, @Reference var user: User) {
	def this() = this("", new Date(), null)
}
