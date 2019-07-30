package cn.sini.cgb.common.util;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import cn.sini.cgb.common.exception.SystemException;
import cn.sini.cgb.common.https.AnyHostnameVerifier;
import cn.sini.cgb.common.https.AnyX509TrustManager;

/**
 * 网络工具类
 * 
 * @author 杨海彬
 */
public class NetUtils {

	/** 默认编码 */
	private static final String DEFAULT_ENCODING = "UTF-8";
	/** 默认超时 */
	private static final int DEFAULT_TIMEOUT = 10000;

	/** 获取指定URL的源数据 */
	public static String getSourceData(String url) {
		return getSourceData(url, "GET", null, DEFAULT_ENCODING, DEFAULT_TIMEOUT, false);
	}

	/** 获取指定URL的源数据 */
	public static String getSourceData(String url, Object postData, boolean formSubmit) {
		return getSourceData(url, "POST", postData, DEFAULT_ENCODING, DEFAULT_TIMEOUT, formSubmit);
	}

	/** 获取指定URL的源数据 */
	public static String getSourceData(String url, String method, Object postData, String encoding, int timeout, boolean formSubmit) {
		try {
			HttpURLConnection connection = getHttpURLConnection(url);
			connection.setConnectTimeout(timeout);
			connection.setDoInput(true);
			connection.setRequestMethod(method);
			if (postData != null) {
				connection.setDoOutput(true);
				if (formSubmit) {
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				} else {
					connection.setRequestProperty("Content-Type", "application/octet-stream");
				}
			}
			connection.connect();
			if (postData != null) {
				IOUtils.write(postData.toString(), connection.getOutputStream(), encoding);
			}
			return IOUtils.toString(connection.getInputStream(), encoding);
		} catch (Exception e) {
			throw new SystemException("获取指定URL的源数据出现异常", e);
		}
	}

	/** 获取连接对象，如果是HTTPS，自动信任证书 */
	private static HttpURLConnection getHttpURLConnection(String url) throws Exception {
		HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
		if (httpURLConnection instanceof HttpsURLConnection) {
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new X509TrustManager[] { new AnyX509TrustManager() }, null);
			((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(context.getSocketFactory());
			((HttpsURLConnection) httpURLConnection).setHostnameVerifier(new AnyHostnameVerifier());
		}
		return httpURLConnection;
	}
}