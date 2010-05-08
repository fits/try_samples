using System;
using DotNetGuru.AspectDNG.Joinpoints;

public class LogAspect
{
	//Calc クラスの全メソッドの呼び出し位置にウィービング
	[AroundCall("* Calc::*(*)")]
	public static object Intercept(OperationJoinPoint jp)
	{
		Console.WriteLine("--- before call : {0}", jp);

		return jp.Proceed();
	}

	//Calc クラスの全プロパティ取得の実行位置にウィービング
	[AroundBody("* Calc::get_*()")]
	public static object InterceptPorperty(MethodJoinPoint jp)
	{
		Console.WriteLine("--- before property get : {0}", jp);

		return jp.Proceed();
	}
}