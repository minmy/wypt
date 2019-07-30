package cn.sini.cgb.admin.identity.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import cn.sini.cgb.common.exception.ParamException;
import cn.sini.cgb.common.http.HttpRequestWrapper;

/**
 * 自定义认证过滤器
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

	/** 构造方法，设置登录URL */
	public CustomAuthenticationFilter(String filterProcessesUrl) {
		super(filterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException, IOException, ServletException {
		HttpRequestWrapper request = new HttpRequestWrapper(req);
		if (!request.getMethod().equalsIgnoreCase("POST")) {
			throw new CustomAuthenticationException("登录失败，非法的登录请求");
		}
		try {
			CustomAuthenticationToken token = new CustomAuthenticationToken(request.getIp(), authenticationDetailsSource.buildDetails(req));
			token.setUsername(request.getTrimMust("username"));
			token.setPassword(request.getStringMust("password"));
			return this.getAuthenticationManager().authenticate(token);
		} catch (CustomAuthenticationException e) {
			throw e;
		} catch (ParamException e) {
			LOGGER.error("登录参数异常，请求参数为[" + request + "]", e);
			throw new CustomAuthenticationException("登录失败，提交的登录参数不正确");
		} catch (Exception e) {
			LOGGER.error("登录过程异常，请求参数为[" + request + "]", e);
			throw new CustomAuthenticationException("登录失败，服务器内部错误，请稍后再试...");
		}
	}
}