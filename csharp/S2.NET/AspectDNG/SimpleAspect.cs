
using System;

using DotNetGuru.AspectDNG.Joinpoints;

using Seasar.Framework.Container;

public class SimpleAspect
{
	[AroundBody("* *.ArgDefImpl::get_Value()")]
	public static object ValueProperty(MethodJoinPoint jp)
	{
		Console.WriteLine("*** target : {0}", jp.RealTarget);

		IArgDef adef = jp.RealTarget as IArgDef;

		if (adef != null)
		{
			Console.WriteLine("expression:{0}, argtype:{1}", adef.Expression, adef.ArgType);
		}

		object result = jp.Proceed();

		return result;
	}
}
