using System;
using System.Linq;

class User
{
	public int Id;
	public string Name;
}

class Address
{
	public int UserId;
	public string AType;
	public string AData;
}

public class JoinSample
{
	public static void Main(string[] args)
	{
		User[] userList = 
		{
			new User() {Id = 1, Name = "test"},
			new User() {Id = 2, Name = "admin"},
		};

		Address[] addrList = 
		{
			new Address() {UserId = 1, AType = "tel", AData = "000-111-2222"},
			new Address() {UserId = 1, AType = "email", AData = "test@aaa"},
			new Address() {UserId = 2, AType = "tel", AData = "999-888-2222"},
		};

		//内部結合
		var query = from u in userList join a in addrList on u.Id equals a.UserId select new {u.Name, Address = a.AData};

		foreach (var ua in query)
		{
			Console.WriteLine("{0}, {1}", ua.Name, ua.Address);
		}

		Console.WriteLine();

		//グループ結合
		var query2 = from u in userList join a in addrList on u.Id equals a.UserId into ua select new {u.Name, Address = ua};

		foreach (var it in query2)
		{
			Console.WriteLine("----- {0} ------", it.Name);

			foreach (var ad in it.Address)
			{
				Console.WriteLine("{0}, {1}", ad.AType, ad.AData);
			}
		}

		Console.WriteLine();

		//左外部結合
		var query3 = from u in userList join a in addrList on u.Id equals a.UserId into ua from b in ua select new {u.Name, Address = b};

		foreach (var it in query3)
		{
			Console.WriteLine("{0}, {1}", it.Name, it.Address.AData);
		}

	}
}