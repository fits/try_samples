using System;

using Seasar.Framework.Container;
using Seasar.Framework.Container.Factory;

public class Test
{
	private const string PATH = "test.dicon";
	
	public static void Main(string[] args)
	{
		IS2Container s2c = S2ContainerFactory.Create(PATH);

		Data2 data = s2c.GetComponent("data2") as Data2;

		Console.WriteLine("id : {0}", data.LinkData.Id);
	}
}