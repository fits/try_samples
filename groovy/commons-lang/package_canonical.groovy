@Grab('commons-beanutils:commons-beanutils:1.9.2')
@Grab('org.apache.commons:commons-lang3:3.3.2')
import org.apache.commons.beanutils.*
import org.apache.commons.lang3.*

println ClassUtils.getPackageCanonicalName(new BeanUtilsBean(), '')
