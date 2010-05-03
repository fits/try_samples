
public class DataManager<T, S> where T : class, IData, new()
{
	public delegate S ExecuteDelegate(T data);

	public S Execute(ExecuteDelegate exeDelegate)
	{
		return exeDelegate(this.CreateData());
	}

	private T CreateData()
	{
		return new T();
	}
}