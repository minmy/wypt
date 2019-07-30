package cn.sini.cgb.admin.identity.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;

/**
 * 自定义登录认证成功处理器
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private RequestCache requestCache = new HttpSessionRequestCache();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
		HttpRequestWrapper request = new HttpRequestWrapper(req);
		HttpResponseWrapper response = new HttpResponseWrapper(res);
		String callbackUrl = request.getString("callbackUrl");
		if (StringUtils.isEmpty(callbackUrl)) {
			DefaultSavedRequest savedRequest = (DefaultSavedRequest) this.requestCache.getRequest(req, response);
			if (savedRequest != null) {
				callbackUrl = savedRequest.getRequestURI();
				String queryString = savedRequest.getQueryString();
				if (StringUtils.isNotEmpty(queryString)) {
					callbackUrl += "?" + queryString;
				}
			} else {
				callbackUrl = request.getContextPath();
				if (StringUtils.isEmpty(callbackUrl)) {
					callbackUrl = "/";
				}
			}
		} else {
			this.requestCache.removeRequest(req, response);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
		if (request.isAjaxRequest()) {
			response.outputJson(true, callbackUrl);
		} else {
			response.sendRedirect(callbackUrl);
		}
	}
}