@Grab('org.apache.velocity:velocity:1.7')
import org.apache.velocity.app.Velocity
import org.apache.velocity.VelocityContext

def tpl = Velocity.getTemplate('sample.vm')
def ctx = new VelocityContext()

ctx.put('product1', 'sample1')
ctx.put('price1', 10)

ctx.put('product2', 'sample2')
ctx.put('price2', 20)

def writer = new StringWriter()

tpl.merge(ctx, writer)

println writer.toString()
