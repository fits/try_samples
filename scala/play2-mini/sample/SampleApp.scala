package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._

object SampleApp extends Application {
	def route = {
		case GET(Path("/")) => Action {
			Ok("sample")
		}
	}
}
