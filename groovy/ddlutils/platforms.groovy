@Grab('org.apache.ddlutils:ddlutils:1.0')
import org.apache.ddlutils.PlatformFactory

PlatformFactory.supportedPlatforms.each { println it }
