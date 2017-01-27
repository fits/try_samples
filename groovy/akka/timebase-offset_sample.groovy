@Grab('com.typesafe.akka:akka-persistence-query_2.12:2.5-M1')
@Grab('com.fasterxml.uuid:java-uuid-generator:3.1.4')
import akka.persistence.query.Offset
import com.fasterxml.uuid.Generators

def uuid = Generators.timeBasedGenerator().generate()
def offset = Offset.timeBasedUUID(uuid)

println offset
