@Grab('commons-beanutils:commons-beanutils:1.8.3')
@Grab('org.springframework:spring-beans:3.2.4.RELEASE')
import org.apache.commons.beanutils.ConvertUtils
import org.apache.commons.beanutils.converters.BigDecimalConverter

import groovy.transform.*

@TupleConstructor
@ToString(includeNames=true)
class Data {
	String name
	BigDecimal value
}

// Commons 版 BeanUtils.copyProperties
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
// Commons 版 BeanUtils.copyProperties with converter
def copyPropsCommons2 = { data ->
	println '----- Commons with converter -----'

	def trg = new Data()

	ConvertUtils.register(new BigDecimalConverter(null), BigDecimal)

	org.apache.commons.beanutils.BeanUtils.copyProperties(trg, data)

	ConvertUtils.deregister()

	println trg
}
// Spring 版 BeanUtils.copyProperties
def copyPropsSpring = { data ->
	println '----- Spring -----'

	def trg = new Data()
	org.springframework.beans.BeanUtils.copyProperties(data, trg)
	println trg
}

def procList = [copyPropsCommons, copyPropsCommons2, copyPropsSpring]

def d1 = new Data('sample1', 100)

procList.each { it(d1) }

println ''

def d2 = new Data('sample2')

procList.each { it(d2) }

println ''

def d3 = [
	name: 'sample3',
	value: 10
]

// Commons 版は Map からのコピーが可能
copyPropsCommons(d3)
// Spring 版は Map からのコピーが不可
copyPropsSpring(d3)
