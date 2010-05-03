using System;
using System.Threading;
using System.Reflection;

namespace TestCOM
{
	public interface ITesting
	{
		string Check(string name);
	}

    public class TestSample : ITesting
    {
        public string Check(string name)
        {
			for (int i = 0; i < 100; i++) {
				Thread.Sleep(100);
			}
            return "result-" + name;
        }
    }
}
