
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

/**
 * 信頼されない証明書の HTTPS でBasic認証を使って POST するサンプル
 */
public class BasicPostNovalidateCertificate {
	public static void main(String[] args) throws Exception {

		URL url = new URL(args[0]);
		final String user = args[1];
		final String pass = args[2];
		String postData = args[3];

		//Basic認証
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass.toCharArray());
			}
		});

		// ホスト名を検証しないようにする設定
		//（openConnection する前に設定しておく必要あり）
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String host, SSLSession ses) {
				return true;
			}
		});

		HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("POST");

		//SSL 証明書を検証しないための設定
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
		BufferedInputStream bis = new BufferedInputStream(is);

		int len = 0;
		byte[] buf = new byte[1024];

		while ((len = bis.read(buf, 0, buf.length)) > -1) {
			System.out.write(buf, 0, len);
		}

		bis.close();
	}
}
