package fits.sample

import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport

class ScalatraMorphiaSample extends ScalatraServlet with ScalateSupport {

	get("/users") {
		
		"sample page"
	}
}

