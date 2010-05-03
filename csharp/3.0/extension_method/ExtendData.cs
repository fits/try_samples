namespace Y
{
	using System;
	using X;

	public static class ExtendData
	{
		public static void ExtMethod(this Data data)
		{
			Console.WriteLine("call extmethod");
		}
	}
}