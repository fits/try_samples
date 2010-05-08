
using System;

public class Calc
{
	private int x;
	private int y;

	public int X
	{
		get
		{
			return this.x;
		}
		set
		{
			this.x = value;
		}
	}

	public int Y
	{
		get
		{
			return this.y;
		}
		set
		{
			this.y = value;
		}
	}

	public int Add()
	{
		return this.x + this.y;
	}

	public static void Main(string[] args)
	{
		Calc calc = new Calc();

		calc.X = 100;
		calc.Y = 50;

		Console.WriteLine("{0} + {1} = {2}", calc.X, calc.Y, calc.Add());
	}
}
