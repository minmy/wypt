package cn.sini.cgb.admin.identity.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义认证异常
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public CustomAuthenticationException(String msg) {
		super(msg);
	}

	public CustomAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}
}