
using System;

public class DataImpl : IData
{
	private string name;
	private int point;

	public DataImpl(string name)
	{
		this.name = name;
	}

	public int Point
	{
		set
		{
			this.point = value;
		}
	}

	public void Print()
	{
		Console.WriteLine("name={0}, point={1}", this.name, this.point);
	}
}
