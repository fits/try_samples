@Grab('com.sparkjava:spark-core:2.5')
@Grab('org.slf4j:slf4j-simple:1.7.21')
import static spark.Spark.*

get('/now') { req, res -> new Date().format('yyyy/MM/dd HH:mm:ss') }
