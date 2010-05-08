
public class DataProcessor
{
	private IData data;

	public IData Data
	{
		set
		{
			this.data = value;
		}
	}

	public void Process()
	{
		this.data.Print();
	}
}
