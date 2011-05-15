package fits.sample

import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport

class ScalatraMorphiaSample extends ScalatraServlet with ScalateSupport {

	beforeAll {
		contentType = "text/html"
	}

	get("/users") {
/*		renderTemplate("user.scaml", 
			"action" -> "/users"
		)
*/
		templateEngine.layout("user.scaml", 
			Map("action" -> "/users")
		)

	}
}

