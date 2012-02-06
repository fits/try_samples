import javax.servlet.http._

class SampleServlet extends HttpServlet {

	val html = 
<html>
<head>
	<title>Scala Servlet Sample</title>
</head>
<body>
	<h1>Sample</h1>
</body>
</html>

	override def doGet(req: HttpServletRequest, res: HttpServletResponse) = {
		res.getWriter().print(html);
	}
}
