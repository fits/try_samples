@Grab('commons-beanutils:commons-beanutils:1.9.2')
import org.apache.commons.beanutils.*

println ConvertUtils.convert('10', Integer)
println ConvertUtils.convert('10', int)

println ConvertUtils.convert('10.5', float)

//println ConvertUtils.convert('2014-08-25', Date)
