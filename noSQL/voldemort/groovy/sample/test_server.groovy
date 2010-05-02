
import voldemort.server.*

def config = VoldemortConfig.loadFromVoldemortHome(args[0])
def server = new VoldemortServer(config)

server.start()

