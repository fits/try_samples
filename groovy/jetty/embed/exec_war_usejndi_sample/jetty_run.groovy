import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

@Grapes([
	@Grab("org.eclipse.jetty:jetty-webapp:7.4.5.v20110725"),
	@Grab("org.eclipse.jetty:jetty-jsp-2.1:7.4.5.v20110725"),
	@Grab("org.eclipse.jetty:jetty-plus:7.4.5.v20110725"),
	@Grab("org.mortbay.jetty:jsp-2.1-glassfish:*")
])
def server = new Server(8088)

def webapp = new WebAppContext()
webapp.contextPath = "/"
webapp.war = args[0]

//jetty-env.xml Çì«Ç›çûÇ‹ÇπÇÈÇΩÇﬂÇÃê›íË
webapp.configurationClasses = [
	"org.eclipse.jetty.webapp.WebInfConfiguration",
	"org.eclipse.jetty.webapp.WebXmlConfiguration",
	"org.eclipse.jetty.webapp.MetaInfConfiguration",
	"org.eclipse.jetty.webapp.FragmentConfiguration",
	"org.eclipse.jetty.plus.webapp.EnvConfiguration",
	"org.eclipse.jetty.plus.webapp.PlusConfiguration",
	"org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
	"org.eclipse.jetty.webapp.TagLibConfiguration"
]

server.handler = webapp

server.start()
server.join()
