using DotNetGuru.AspectDNG.Joinpoints;

[ImplementInterface("Source")]
public interface IChecker
{
	string Check(int data, string msg);
}
