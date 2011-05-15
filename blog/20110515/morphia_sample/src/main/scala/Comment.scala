package fits.sample

import java.util.Date
import com.google.code.morphia.annotations._

class Comment(var content: String, @Reference var user: User) {

	//デフォルトコンストラクタは必須
	def this() = this("", null)

	var createdDate: Date = new Date()
}
