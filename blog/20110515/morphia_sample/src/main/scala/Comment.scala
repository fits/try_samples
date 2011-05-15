package fits.sample

import java.util.Date
import com.google.code.morphia.annotations._

class Comment {

	def this(content: String, user: User) = {
		this()
		this.content = content
		this.user = user
	}

	var content: String = ""
	@Reference var user: User = null

	var createdDate: Date = new Date()
}
