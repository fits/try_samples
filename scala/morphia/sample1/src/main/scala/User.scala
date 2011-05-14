package fits.sample

import com.google.code.morphia.annotations._

@Entity
class User(@Property var name: String) {
	def this() = this("")
}
