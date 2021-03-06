using System;
using System.Text;
using Microsoft.VisualBasic.FileIO;

class CSVParse
{
	public static void Main(string[] args)
	{
		using (var reader = new TextFieldParser(args[0], Encoding.Default))
		{
			//区切り文字を設定する必要あり
			reader.SetDelimiters(",");

			while (!reader.EndOfData)
			{
				var r = reader.ReadFields();
				Console.WriteLine("{0} : {1}", r[0], r[2]);
			}
		}
	}
}