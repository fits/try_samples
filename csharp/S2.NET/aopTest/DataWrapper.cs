
public class DataWrapper
{
	private IData target;

	public IData Target
	{
		set
		{
			this.target = value;
		}
		get
		{
			return this.target;
		}
	}
}