using System;
using System.Web;

class UrlEncode
{
	static void Main(string[] args)
	{
		var str = ";/?:@=&% $-_.+!*'\"(),{}|\\^~[]";

		// ;/?:@=&%25%20$-_.+!*'%22(),%7B%7D%7C%5C%5E~%5B%5D
		Console.WriteLine(Uri.EscapeUriString(str));

		// %3b%2f%3f%3a%40%3d%26%25+%24-_.%2b!*%27%22()%2c%7b%7d%7c%5c%5e%7e%5b%5d
		Console.WriteLine(HttpUtility.UrlEncode(str));
	}
}
