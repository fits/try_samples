
import org.jboss.aop.*
import org.jboss.aop.advice.*
import org.jboss.aop.classpool.*
import org.jboss.aop.joinpoint.*

def addAdvice(pointcut, interceptor) {

    bind = new AdviceBinding(pointcut, null)
    bind.addInterceptor(interceptor)

    AspectManager.instance().addBinding(bind)

}

addAdvice("execution(* *->addData(..))", SimpleInterceptor)

class TestInterceptor implements Interceptor {
    def String getName() {
        "TestInterceptor"
    }

    def invoke(Invocation inv) {
        println "-------- before -------------"

        def result = inv.invokeNext()

        println "-------- after -------------"

        result
    }
}

@Aspect class TestAspect {

    @Bind(pointcut = "execution(* *->printData(..))")
    def testAdvice(MethodInvocation inv) {
        println "*** test aspect ***"

        inv.invokeNext
    }
}


addAdvice("execution(* *->printData(..))", TestInterceptor)

/*
ad = new AspectDefinition("testaspect", null, new GenericAspectFactory("TestAspect", null))

AspectManager.instance().addInterceptorFactory("testAdvice", new AdviceFactory(ad, "testAdvice"))
*/

/*
//RuntimeException TestAspect class is frozen and pruned Ç™î≠ê∂
apr = AOPClassPoolRepository.getInstance()
apr.registerClass(TestAspect)

acp = new AOPClassPoolFactory().create(null, apr)
cf = acp.get("TestAspect").getClassFile()

new AspectAnnotationLoader(AspectManager.instance()).deployClassFile(cf)
*/


data = new TestData()
data.addData("test", 1)

data.printData()
