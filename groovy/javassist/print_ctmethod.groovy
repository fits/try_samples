@Grab('org.javassist:javassist:3.18.1-GA')
import javassist.*

def pool = ClassPool.default
def cls = pool.get 'java.lang.String'

cls.declaredMethods.each {
	println it
	println it.methodInfo.descriptor
}
