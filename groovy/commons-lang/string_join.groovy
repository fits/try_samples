@Grab('commons-lang:commons-lang:2.6')
import org.apache.commons.lang.StringUtils

println StringUtils.join(['A', null, '1', 'B'], '-')
