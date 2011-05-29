
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

val server = new Server(8080)

val webapp = new WebAppContext()
webapp.setContextPath("/")
webapp.setWar(args(0))

server.setHandler(webapp)

server.start
server.join
