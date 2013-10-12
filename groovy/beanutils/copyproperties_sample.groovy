@GrabResolver(name = 'seasar.org', root = 'http://maven.seasar.org/maven2')
@Grab('commons-beanutils:commons-beanutils:1.8.3')
@Grab('org.springframework:spring-beans:3.2.4.RELEASE')
@Grab('org.seasar.container:s2-framework:2.4.46')
import org.apache.commons.beanutils.ConvertUtils
import org.apache.commons.beanutils.converters.BigDecimalConverter

import groovy.transform.*

@TupleConstructor
@ToString(includeNames=true)
class Data {
	String name
	BigDecimal value
	DataItem item
}

class DataItem {
}

// Spring ”Å BeanUtils.copyProperties
def copyPropsSpring = { data ->
	println '----- Spring -----'

	def trg = new Data()
	org.springframework.beans.BeanUtils.copyProperties(data, trg)
	println trg
}
// Commons ”Å BeanUtils.copyProperties
def copyPropsCommons = { data ->
	println '----- Commons -----'

	try {
		def trg = new Data()
		org.apache.commons.beanutils.BeanUtils.copyProperties(trg, data)
		println trg
	} catch (e) {
		println e
	}
}
// Commons ”Å BeanUtils.copyProperties with converter
def copyPropsCommons2 = { data ->
	println '----- Commons with converter -----'

	def trg = new Data()

	ConvertUtils.register(new BigDecimalConverter(null), BigDecimal)

	org.apache.commons.beanutils.BeanUtils.copyProperties(trg, data)

	ConvertUtils.deregister()

	println trg
}
// Seasar2 ”Å BeanUtil.copyProperties
def copyPropsSeasar = { data ->
	println '----- Seasar2 -----'

	def trg = new Data()
	org.seasar.framework.beans.util.BeanUtil.copyProperties(data, trg)
	println trg
}


def cmdList = [copyPropsSpring, copyPropsCommons, copyPropsCommons2, copyPropsSeasar]

def d1 = new Data('sample1', 100, new DataItem())

println d1

cmdList.each { it(d1) }

println ''

def d2 = new Data('sample2')

println d2

cmdList.each { it(d2) }
