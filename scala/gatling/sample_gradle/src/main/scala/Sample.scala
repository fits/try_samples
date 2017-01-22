
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class Sample extends Simulation {
	val httpConf = http.baseURL("http://localhost:8081/")

	val scn = scenario("First").exec(
		http("top").get("/")
	)

	// setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
	setUp(scn.inject(rampUsers(100) over(10 seconds))).protocols(httpConf)
}
