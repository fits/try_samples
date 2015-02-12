@Grab('org.glassfish:javax.el:3.0.1-b05')
import javax.el.*

def proc = new ELProcessor()

proc.defineBean('x', 10)

println proc.eval('x * 2 + 5')
