
var http = require("http");

http.createServer(function(req, res) {
	var url = require("url").parse(req.url, true);

	console.log("requested : " + url.href);

	var title = (url.query && url.query["test"])? url.query["test"]: "test sample";

	res.writeHead(200, {"Content-Type": "text/html"});
	res.end("<html><body><h1>" + title + "</h1></body></html>");

}).listen(8080, "127.0.0.1");

console.log("server started http://127.0.0.1:8080/");
