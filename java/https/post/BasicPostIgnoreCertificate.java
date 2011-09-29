
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 * Basic認証を使って SSL の証明書を無視して HTTPS で POST するサンプル
 *
 */
public class BasicPostIgnoreCertificate {
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
			System.out.println("java BasicPostIgnoreCertificate <url> <basic user> <basic password> <post data>");
			return;
		}

		//https の URL
		URL url = new URL(args[0]);

		final String user = args[1];
		final String pass = args[2];
		String postData = args[3];

		//Basic認証用の設定
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});

		//SSL 証明書を無視するための設定
		//（openConnection する前に設定しておく必要あり）
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String host, SSLSession ses) {
				return true;
			}
		});

		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");

		//SSL 証明書を無視するための設定
		SSLContext sslctx = SSLContext.getInstance("SSL");
		sslctx.init(null, new X509TrustManager[] {
			new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
				}
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
				}
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			}
		}, new SecureRandom());

		con.setSSLSocketFactory(sslctx.getSocketFactory());

		//POSTデータの出力
		OutputStream os = con.getOutputStream();
		PrintStream ps = new PrintStream(os);
		ps.print(postData);
		ps.close();

		//結果の出力
		InputStream is = con.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;

		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}

		reader.close();
	}
}
