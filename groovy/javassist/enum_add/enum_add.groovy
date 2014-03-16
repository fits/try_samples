@Grab('org.javassist:javassist:3.18.1-GA')
import javassist.*

def pool = ClassPool.default
pool.appendClassPath('.')

def cc = pool.get 'EType'

def secondField = CtField.make('public static final EType Second = new EType("Second", 1);', cc)
cc.addField(secondField)

cc.classInitializer.insertAfter('$VALUES = new EType[] { First, Second };')


cc.toClass()

println EType.Second

println EType.values()

println EType.valueOf('Second')
