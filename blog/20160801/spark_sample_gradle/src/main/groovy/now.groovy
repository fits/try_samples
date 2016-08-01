import static spark.Spark.*

get('/now') { req, res -> new Date().format('yyyy/MM/dd HH:mm:ss') }
