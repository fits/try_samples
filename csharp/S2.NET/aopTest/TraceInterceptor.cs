
using System;
using Seasar.Framework.Aop;
using Seasar.Framework.Aop.Interceptors;

public class TraceInterceptor : AbstractInterceptor
{
	public override object Invoke(IMethodInvocation inv)
	{
		Console.WriteLine("invoke method : {0}", inv.Method);
		return inv.Proceed();
	}

}