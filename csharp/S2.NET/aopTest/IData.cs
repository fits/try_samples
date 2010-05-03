
public interface IData
{
	string Id {get;}
	string Name {get;}
	string Note {get;set;}
	int Point {get;}

	string GetMessage(string msg);
}