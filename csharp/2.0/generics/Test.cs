using System;

public class Test
{
	public static void Main(string[] args)
	{
		DataManager<DataImpl, string> manager = new DataManager<DataImpl, string>();
		string result = manager.Execute(
			delegate(DataImpl data)
			{
				return "check: " + data.getMessage();
			}
		);

		Console.WriteLine(result);
	}

}