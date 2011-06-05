
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

def server = new Server(8080)

def webapp = new WebAppContext()
webapp.contextPath = "/"
webapp.war = args[0]

server.handler = webapp

server.start()
server.join()

