package cn.sini.cgb.admin.identity.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 自定义CSRF拦截地址匹配器
 * 
 * @author 杨海彬
 */
public class CustomCsrfMatcher implements RequestMatcher {

	/** 不拦截的请求方法 */
	private final Set<String> allowedMethods = new HashSet<String>(Arrays.asList("GET"));

	/** 不拦截的URL */
	private Set<String> allowedUrls = new HashSet<String>();

	/**
	 * 设置不拦截的URL
	 * 
	 * @param allowedUrls 不拦截的URL
	 */
	public void setAllowedUrls(Set<String> allowedUrls) {
		this.allowedUrls = allowedUrls;
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		if (this.allowedMethods.contains(request.getMethod())) {
			return false;
		} else {
			String url = request.getRequestURI().replaceFirst(request.getContextPath(), "");
			if (this.allowedUrls.contains(url)) {
				return false;
			}
		}
		return true;
	}
}