
using System;
using System.Data.OleDb;

// Oracle Provider for OLE DB（OraOLEDB）を使った DB 接続サンプル
class OleDbSample
{
	public static void Main(string[] args)
	{
		using (var con = new OleDbConnection("Provider=OraOLEDB.Oracle; Data Source=(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=127.0.1.2)(PORT=1522)))(CONNECT_DATA=(SERVICE_NAME=TEST))); User ID=U1; Password=P1"))
		{
			try
			{
				con.Open();

				using (var cmd = new OleDbCommand("select * from CUSTOMER", con)) 
				{
					var reader = cmd.ExecuteReader();

					while (reader.Read())
					{
						Console.WriteLine("customer : {0}", reader.GetString(0)));
					}

					reader.Close();
				}
			}
			catch (Exception ex) {
				Console.WriteLine(ex.StackTrace);
			}
		}
	}
}

