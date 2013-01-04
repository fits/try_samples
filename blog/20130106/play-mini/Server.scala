package fits.sample

import com.typesafe.play.mini._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._

object Server extends Application {

	def route = Routes(
		Through("/user/([^/]*)".r) { groups: List[String] =>
			Action {
				val id :: Nil = groups

				Ok(Json.toJson {
					Map(
						"id" -> id,
						"name" -> "play-mini sample"
					)
				})
			}
		},
		{
			case POST(Path("/user")) => Action { req =>
				val data = req.body.asJson
				data.foreach(println)

				Ok("")
			}
		}
	)
}
