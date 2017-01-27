@Grab('com.fasterxml.uuid:java-uuid-generator:3.1.4')
import com.fasterxml.uuid.Generators

def uuid = Generators.timeBasedGenerator().generate()

println uuid
