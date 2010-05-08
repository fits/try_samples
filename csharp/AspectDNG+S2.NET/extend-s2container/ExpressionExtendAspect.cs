
using System;
using System.Collections;
using System.Configuration;

using DotNetGuru.AspectDNG.Joinpoints;

using Seasar.Framework.Container;
using Spring.Expressions;

public class ExpressionExtendAspect
{
	[AroundBody("* *.ArgDefImpl::get_Value()")]
	public static object ValueProperty(MethodJoinPoint jp)
	{
		object result = null;

		IArgDef adef = jp.RealTarget as IArgDef;
		string expression = adef.Expression;

		if (expression != null && expression.IndexOf("#") > -1)
		{
			Hashtable table = new Hashtable();
			table["container"] = adef.Container;
			table["appSettings"] = ConfigurationManager.AppSettings;

			result = ExpressionEvaluator.GetValue(null, expression, table);
		}
		else
		{
			result = jp.Proceed();
		}

		return result;
	}
}
