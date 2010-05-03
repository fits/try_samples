
using System;

public class Data : MarshalByRefObject, IData
{
	private string id;
	private string name;
	private string note;
	private int point;

	public Data()
	{
	}

	public Data(string id)
	{
		this.id = id;
	}

	public string Id
	{
		get
		{
			return this.id;
		}
		set
		{
			this.id = value;
		}
	}

	public string Name
	{
		get
		{
			return this.name;
		}
		set
		{
			this.name = value;
		}
	}

	public string Note
	{
		get
		{
			return this.note;
		}
		set
		{
			this.note = value;
		}
	}

	public int Point
	{
		get
		{
			return this.point;
		}
		set
		{
			this.point = value;
		}
	}

	public string GetMessage(string msg)
	{
		return msg + "aaaaaaa";
	}
}