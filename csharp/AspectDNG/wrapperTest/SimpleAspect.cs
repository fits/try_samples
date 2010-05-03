
using System;
using System.Reflection;

using DotNetGuru.AspectDNG.Joinpoints;

public class SimpleAspect
{
	[Insert("DataWrapper")]
	private static IData target = new DataImpl();

	[AroundBody("//Method[match('* DataWrapper::*(*)')]")]
	public static object CallMethod(MethodJoinPoint jp)
	{
		object result = null;

		PrintJoinPoint(jp);

		MethodInfo mi = target.GetType().GetMethod(jp.TargetOperationName);
		result = mi.Invoke(target, jp.Arguments);

		return result;
	}

	private static void PrintJoinPoint(JoinPoint jp)
	{
		Console.WriteLine("joinpoint : {0}", jp);
	}
}
