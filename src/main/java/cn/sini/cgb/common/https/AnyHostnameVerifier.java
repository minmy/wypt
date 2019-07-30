package cn.sini.cgb.common.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 任意主机名验证器
 * 
 * @author 杨海彬
 */
public class AnyHostnameVerifier implements HostnameVerifier {

	@Override
	public boolean verify(final String hostname, final SSLSession session) {
		return true;
	}
}