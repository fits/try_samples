<%@ WebService Language="C#" Class="Test.Test1" %>

using System;
using System.Web.Services;

namespace Test
{
	public class Test1
	{
		[WebMethod]
		public string GetMessage()
		{
			return "‚±‚ñ‚É‚¿‚Í";
		}


		[WebMethod]
		public int GetCount()
		{
			return 1;
		}

		[WebMethod]
		public string[] GetMessages()
		{
			return new string[]{"test1", "test2", "test3"};
		}

		[WebMethod]
		public Data GetData(string msg)
		{
			Data result = new Data();
			result.name = "hello " + msg;
			result.score = DateTime.Now.Second;
			return result;
		}
	}

	public class Data
	{
		public string name;
		public int score;
	}

}
