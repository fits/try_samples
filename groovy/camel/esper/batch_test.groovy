
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {
		from("direct:start").to("esper:test")

		//1秒毎に発生したイベントの発生数と value の合計を出力
		from("esper:test?eql=select count(*) as counter, sum(value) as total from TestEvent.win:time_batch(1 sec)").process({println "[time]   counter: ${it.in.body.counter}, total: ${it.in.body.total}"} as Processor)

		//value が 5以上のイベントが 3つ発生したら
		//イベントの発生数と value の合計を出力
		from("esper:test?eql=select count(*) as counter, sum(value) as total from TestEvent(value >= 5).win:length_batch(3)").process({println "[length] counter: ${it.in.body.counter}, total: ${it.in.body.total}"} as Processor)

	}
}

//イベントクラス
class TestEvent {
	String name
	int value
}

ctx = new DefaultCamelContext()
ctx.addRoutes(new SampleRoute())

template = ctx.createProducerTemplate()

println "start"

ctx.start()

(1..10).each {
	template.sendBody("direct:start", new TestEvent(name: "test${it}", value: Math.random() * 10))

	Thread.sleep((int)(Math.random() * 1000))
}

Thread.sleep(1000)

ctx.stop()

println "stop"
