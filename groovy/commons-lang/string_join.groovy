@Grab('org.apache.commons:commons-lang3:3.3.2')
import org.apache.commons.lang3.StringUtils

println StringUtils.join(['A', null, '1', 'B'], '-')
