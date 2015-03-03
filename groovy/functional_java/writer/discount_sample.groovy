@Grab('org.functionaljava:functionaljava:4.3')
import fj.F
import fj.Monoid
import fj.data.List as FList
import fj.data.Writer

def show = { p -> println p.run() }

def discount = { BigDecimal discountValue ->
	{ BigDecimal curValue -> 
		Writer.unit(curValue - discountValue, FList.single(curValue), Monoid.listMonoid())
	} as F
}

def price = Writer.unit(1000, Monoid.listMonoid())

show price

show price
	.flatMap(discount(200))
	.flatMap(discount(30))
	.flatMap(discount(160))
