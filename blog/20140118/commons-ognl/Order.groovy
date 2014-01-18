import groovy.transform.*

@CompileStatic
class Order {
	List<OrderLine> lines = []
}

@CompileStatic
@Immutable
class OrderLine {
	String code
	BigDecimal price = 0
}
