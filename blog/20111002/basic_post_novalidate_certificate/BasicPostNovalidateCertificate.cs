using System;
using System.Net;

/**
 * 信頼されない証明書の HTTPS でBasic認証を使って POST するサンプル
 */
class BasicPostNovalidateCertificate
{
	public static void Main(string[] args)
	{
		var url = args[0];
		var user = args[1];
		var pass = args[2];
		var postData = args[3];

		//SSL 証明書を検証しないようにする設定
		//（証明書を何でも受け入れるようにする）
		ServicePointManager.ServerCertificateValidationCallback = 
			(sender, cert, chain, errors) => true;

		using (WebClient wc = new WebClient())
		{
			//Basic認証
			wc.Credentials = new NetworkCredential(user, pass);

			//Post
			var res = wc.UploadString(url, "POST", postData);

			//結果の出力
			Console.Write(res);
		}
	}
}
