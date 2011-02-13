
var http = require("http");

http.createServer(function(req, res) {
	res.writeHead(200, {"Content-Type": "text/html"});
	res.end("<html><body><h1>test sample</h1></body></html>");

}).listen(8080, "127.0.0.1");

console.log("server started http://127.0.0.1:8080/");
