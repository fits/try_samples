
import org.neo4j.kernel.EmbeddedGraphDatabase
import org.neo4j.remote.transports.LocalGraphDatabase
import org.neo4j.remote.transports.RmiTransport

def db = new EmbeddedGraphDatabase("remote-store")

RmiTransport.register(new LocalGraphDatabase(db), "rmi://localhost/remote-test")

addShutdownHook {
	println("shutdown db ...")
	db.shutdown()
}

println "start server ... "
println "press key to stop"

System.in.read()
System.exit(0)
