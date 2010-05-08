
using System;

public class TestDataImpl : IData
{
	private string testName;

	public TestDataImpl(string testName)
	{
		this.testName = testName;
	}

	public void Print()
	{
		Console.WriteLine("TestDataImpl testname={0}", this.testName);
	}
}