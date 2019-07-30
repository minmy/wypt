package cn.sini.cgb.admin.identity.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import cn.sini.cgb.common.http.HttpRequestWrapper;
import cn.sini.cgb.common.http.HttpResponseWrapper;

/**
 * 自定义退出登录处理器
 * 
 * @author 杨海彬
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication authentication) throws IOException, ServletException {
		HttpRequestWrapper request = new HttpRequestWrapper(req);
		HttpResponseWrapper response = new HttpResponseWrapper(res);
		String callbackUrl = request.getString("callbackUrl");
		if (StringUtils.isEmpty(callbackUrl)) {
			callbackUrl = request.getContextPath();
			if (StringUtils.isEmpty(callbackUrl)) {
				callbackUrl = "/";
			}
		}
		if (request.isAjaxRequest()) {
			response.outputJson(true, callbackUrl);
		} else {
			response.sendRedirect(callbackUrl);
		}
	}
}