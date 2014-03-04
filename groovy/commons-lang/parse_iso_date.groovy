@Grab('org.apache.commons:commons-lang3:3.2.1')
import org.apache.commons.lang3.time.DateFormatUtils

// —á. 2014-03-23T07:05:04+09:00
println DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.parse(args[0])

println Date.parse("yyyy-MM-dd'T'HH:mm:ssX", args[0])
