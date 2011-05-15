package fits.sample

import java.util.{List, ArrayList}
import com.google.code.morphia.annotations._
import org.bson.types.ObjectId

@Entity(value = "books", noClassnameStored = true)
class Book(@Property var title: String, @Property var isbn: String) {

	//デフォルトコンストラクタは必須
	def this() = this("", "")

	@Id
	var id: ObjectId = null

	@Embedded
	var comments: List[Comment] = new ArrayList[Comment]()

}