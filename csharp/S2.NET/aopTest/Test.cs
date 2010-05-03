using System;

using Seasar.Framework.Container;
using Seasar.Framework.Container.Factory;

public class Test
{
	private const string PATH = "test.dicon";
	
	public static void Main(string[] args)
	{
		IS2Container s2c = S2ContainerFactory.Create(PATH);

		IData data1 = s2c.GetComponent(typeof(IData)) as IData;
		Console.WriteLine("---- GetComponent(typeof)------");
		PrintData(data1);

		IData data2 = s2c.GetComponent("data") as IData;
		Console.WriteLine("---- GetComponent(data)------");
		PrintData(data2);

		IData data3 = (s2c.GetComponent("wrapper") as DataWrapper).Target;
		Console.WriteLine("---- wrapper ------");
		PrintData(data3);
	}

	private static void PrintData(IData data)
	{
		data.Note = "abc";

		Console.WriteLine("data : {0}, {1}, {2}, {3}", data.Id, data.Name, data.Note, data.Point);
	
		Console.WriteLine("msg : {0}", data.GetMessage("1"));
	}
}