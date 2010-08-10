package fits.sample

import org.scalatra.ScalatraServlet

class ScalatraSample extends ScalatraServlet {
	
	get("/") {
		"sample page"
	}

	get("/:name/:value") {
		val name = params("name")
		val value = params("value")

		"sample page name:" + name + ", value:" + value
	}
}

