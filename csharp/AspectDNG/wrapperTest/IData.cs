
public interface IData
{

	void CheckAudio(string bstrWMX, out int plPeakMin, out int plPeakMax);

	void Print();

	string Test(string data, int i);
}