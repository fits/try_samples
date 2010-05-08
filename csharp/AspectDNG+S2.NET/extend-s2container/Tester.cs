using System;

using Seasar.Framework.Container;
using Seasar.Framework.Container.Factory;

public class Tester
{
	private const string PATH = "app.dicon";
	
	public static void Main(string[] args)
	{
		IS2Container s2c = S2ContainerFactory.Create(PATH);

		DataProcessor processor = s2c.GetComponent("processor") as DataProcessor;
		processor.Process();
	}
}