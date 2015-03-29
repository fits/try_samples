@GrabConfig(systemClassLoader = true)
@Grab('org.flywaydb:flyway-core:3.2.1')
@Grab('mysql:mysql-connector-java:5.1.35')
import org.flywaydb.core.Flyway

def flyway = new Flyway()

flyway.setDataSource(args[0], null, null)

flyway.setLocations 'filesystem:sql'

flyway.migrate()

println '----------'

flyway.info().all().each {
	println "version: ${it.version}, type: ${it.type}, script: ${it.script}, description: ${it.description}, state: ${it.state}, installedOn: ${it.installedOn}"
}
