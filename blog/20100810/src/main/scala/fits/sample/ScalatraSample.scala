package fits.sample

import org.scalatra.ScalatraServlet

class ScalatraSample extends ScalatraServlet {
	
	get("/") {
		"test page"
	}

	get("/:name/:value") {
		val name = params("name")
		val value = params("value")

		"test page : " + name + " - " + value
	}
}

