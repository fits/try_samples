using System;
using System.Configuration;

using Spring.Context.Support;
using Spring.Expressions;

public class Test
{
	public static void Main(string[] args)
	{
		Console.WriteLine(ConfigurationManager.AppSettings["name"]);

		try
		{
			Console.WriteLine("result : {0}", ExpressionEvaluator.GetValue(null, "ConfigurationManager.AppSettings['name']"));
		}
		catch (Exception ex)
		{
			Console.WriteLine(ex);
		}

		try
		{
			Console.WriteLine("result : {0}", ExpressionEvaluator.GetValue(null, "type('System.Configuration.ConfigurationManager')"));
		}
		catch (Exception ex)
		{
			Console.WriteLine(ex);
		}


		TypeRegistry.RegisterType("ConfigurationManager", typeof(ConfigurationManager));
		Console.WriteLine("result : {0}", ExpressionEvaluator.GetValue(null, "ConfigurationManager.AppSettings['name'] + '=testdata'"));

	}

}