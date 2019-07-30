package cn.sini.cgb.admin.identity.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * 自定义认证入口点
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

	/**
	 * 构造方法
	 * 
	 * @param loginUrl 登录页
	 */
	public CustomAuthenticationEntryPoint(String loginUrl) {
		super(loginUrl);
	}

	@Override
	protected String determineUrlToUseForThisRequest(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) {
		return super.determineUrlToUseForThisRequest(req, res, exception);
	}
}