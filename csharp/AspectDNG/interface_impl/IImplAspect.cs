using System;
using DotNetGuru.AspectDNG.Joinpoints;

public class IImplAspect : IChecker
{
	[AroundCall("* Source::Test*(*)")]
	public static object Intercept(OperationJoinPoint jp)
	{
		Console.WriteLine("--- before call : {0}", jp);
		return jp.Proceed();
	}

	[Insert("Source")]
	public string Check(int data, string msg)
	{
		return "...check";
	}

	[Insert("Source")]
	public void PrintLog()
	{
		Console.WriteLine("***** print log *****");
	}


}
