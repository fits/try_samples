
require 'java'

module El
	include_package "javax.el"
	include_package "de.odysseus.el"
	include_package "de.odysseus.el.util"
end

factory = El::ExpressionFactoryImpl.new
ctx = El::SimpleContext.new

ctx.setVariable("point", factory.createValueExpression(100, Java::JavaClass.for_name("int")))

e = factory.createValueExpression(ctx, "${point + 3}", Java::JavaClass.for_name("int"))

p e.getValue(ctx)

