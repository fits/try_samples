
using System;
using Seasar.Framework.Aop;
using Seasar.Framework.Aop.Interceptors;

public class TestInterceptor : AbstractInterceptor
{
	private string append = "";

	public string AppendString
	{
		set
		{
			this.append = value;
		}
	}

	public override object Invoke(IMethodInvocation inv)
	{
		object result = inv.Proceed();

		if (result is string)
		{
			result = (string)result + this.append;
		}

		return result;
	}

}