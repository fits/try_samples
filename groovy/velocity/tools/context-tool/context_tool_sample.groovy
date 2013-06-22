@Grab('org.apache.velocity:velocity:1.7')
@Grab('org.apache.velocity:velocity-tools:2.0')
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.tools.ToolContext
import org.apache.velocity.tools.Toolbox
import org.apache.velocity.tools.ToolInfo

def template = '''product,price
"$!product1",$!price1
#foreach ($index in [2..3])
#if($context.contains("product$index"))
#set($product = $context.get("product$index"))
#set($price = $context.get("price$index"))
#if($price gt 20)
"$product",$price
#end
#end
#end
---
'''

def engine = new VelocityEngine()
engine.init()

def ctx = new ToolContext()
ctx.addToolbox(new Toolbox([
	context: new ToolInfo("context", org.apache.velocity.tools.generic.ContextTool)
]))

ctx.put('product1', 'sample1')
ctx.put('price1', 10)

ctx.put('product2', 'sample2')
ctx.put('price2', 20)

ctx.put('product3', 'a')
ctx.put('price3', 30)

def writer = new StringWriter()

engine.evaluate(ctx, writer, "context_tool_sample", new StringReader(template))

println writer.toString()
