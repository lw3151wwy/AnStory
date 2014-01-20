package com.weibo.sdk.android.net;

import android.graphics.Bitmap;
import android.text.TextUtils;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.util.Utility;
import com.weibo.sdk.android.util.Utility.UploadImageUtils;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpManager
{
  private static final String BOUNDARY = getBoundry();
  private static final String MP_BOUNDARY = "--" + BOUNDARY;
  private static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
  private static final String MULTIPART_FORM_DATA = "multipart/form-data";
  private static final String HTTPMETHOD_POST = "POST";
  public static final String HTTPMETHOD_GET = "GET";
  private static final int SET_CONNECTION_TIMEOUT = 5000;
  private static final int SET_SOCKET_TIMEOUT = 20000;

	public static String openUrl(String url, String method, WeiboParameters params, String file) throws WeiboException {
		String result = "";
		try {
			HttpClient client = getNewHttpClient();
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			client.getParams().setParameter("http.route.default-proxy", NetStateManager.getAPN());
			if (method.equals("GET")) {
				url = url + "?" + Utility.encodeUrl(params);
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (method.equals("POST")) {
				HttpPost post = new HttpPost(url);
				request = post;
				byte[] data = null;
				String _contentType = params.getValue("content-type");
				
				bos = new ByteArrayOutputStream();
				if (!TextUtils.isEmpty(file)) {
					System.out.println("文件不为空");
					paramToUpload(bos, params);
					post.setHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//					Utility.UploadImageUtils.revitionPostImageSize(file);
//					
					imageContentToUpload(bos, file);
				} else {
					System.out.println("文件为空");
					if (_contentType != null) {
						params.remove("content-type");
						post.setHeader("Content-Type", _contentType);
					} else {
						post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					}

					String postParam = Utility.encodeParameters(params);
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();

			if (statusCode != 200) {
				result = readHttpResponse(response);
				throw new WeiboException(result, statusCode);
			}
			result = readHttpResponse(response);
			return result;
		} catch (IOException e) {
			throw new WeiboException(e);
		}
	}

	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			org.apache.http.conn.ssl.SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 20000);
			HttpClient client = new DefaultHttpClient(ccm, params);

			return client;
		} catch (Exception e) {
		}
		return new DefaultHttpClient();
	}

	private static void paramToUpload(OutputStream baos, WeiboParameters params) throws WeiboException {
		String key = "";
		for (int loc = 0; loc < params.size(); ++loc) {
			key = params.getKey(loc);
			StringBuilder temp = new StringBuilder(10);
			temp.setLength(0);
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key).append("\"\r\n\r\n");
			temp.append(params.getValue(key)).append("\r\n");
			byte[] res = temp.toString().getBytes();
			try {
				baos.write(res);
			} catch (IOException e) {
				throw new WeiboException(e);
			}
		}
	}

	private static void imageContentToUpload(OutputStream out, String imgpath) throws WeiboException {
		if (imgpath == null) {
			return;
		}
		StringBuilder temp = new StringBuilder();

		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"pic\"; filename=\"").append("news_image").append("\"\r\n");
		String filetype = "image/png";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		FileInputStream input = null;
		try {
			out.write(res);
			input = new FileInputStream(imgpath);
			byte[] buffer = new byte[51200];
			while (true) {
				int count = input.read(buffer);
				if (count == -1) {
					break;
				}
				out.write(buffer, 0, count);
			}
			out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new WeiboException(e);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					throw new WeiboException(e);
				}
		}
	}

	private static String readHttpResponse(HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		try {
			InputStream inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if ((header != null) && (header.getValue().toLowerCase().indexOf("gzip") > -1)) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			return result;
		} catch (IllegalStateException localIllegalStateException) {
		} catch (IOException localIOException) {
		}
		return result;
	}

	static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; ++t) {
			long time = System.currentTimeMillis() + t;
			if (time % 3L == 0L)
				_sb.append((char) (int) time % '\t');
			else if (time % 3L == 1L)
				_sb.append((char) (int) (65L + time % 26L));
			else {
				_sb.append((char) (int) (97L + time % 26L));
			}
		}
		return _sb.toString();
	}

	private static class MySSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			this.sslContext.init(null, new TrustManager[] { tm }, null);
		}

		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return this.sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		public Socket createSocket() throws IOException {
			return this.sslContext.getSocketFactory().createSocket();
		}
	}
}
