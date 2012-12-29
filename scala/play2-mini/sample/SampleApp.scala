package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._

object SampleApp extends Application {
	def route = Routes(
		{
			case GET(Path("/")) => Action {
				Ok("sample")
			}
		},
		// "/a/" 以降を / で区切ってリスト化したものが groups となる
		Through("/a/") { groups: List[String] =>
			Action {
				println(groups)
				Ok("params : " + groups)
			}
		},
		// "/b/xxx" の xxx 部分が groups となる
		Through("/b/(.*)".r) { groups: List[String] =>
			Action {
				println(groups)
				Ok("params : " + groups)
			}
		}
	)
}
