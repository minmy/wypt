package cn.sini.cgb.admin.identity.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.common.entity.EntityUtils;
import cn.sini.cgb.common.exception.SystemException;

/**
 * 自定义认证Token对象
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	/** Principal */
	private Object principal;

	/** 登录IP */
	private String loginIp;

	/** 帐号 */
	private String username;

	/** 密码 */
	private String password;

	/** 构造方法，未通过登录认证 */
	public CustomAuthenticationToken(String loginIp, Object details) {
		super(null);
		this.loginIp = loginIp;
		super.setDetails(details);
		super.setAuthenticated(false);
	}

	/** 构造方法，已通过登录认证 */
	public CustomAuthenticationToken(User user, CustomAuthenticationToken token) {
		super(user.getAuthorities());
		this.principal = user;
		this.loginIp = token.getLoginIp();
		super.setDetails(token.getDetails());
		super.setAuthenticated(true);
	}

	/**
	 * 获取Principal
	 * 
	 * @return principal Principal
	 */
	public Object getPrincipal() {
		return principal;
	}

	/**
	 * 获取登录IP
	 * 
	 * @return loginIp 登录IP
	 */
	public String getLoginIp() {
		return loginIp;
	}

	/**
	 * 获取帐号
	 * 
	 * @return username 帐号
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置帐号
	 * 
	 * @param username 帐号
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取密码
	 * 
	 * @return password 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 * 
	 * @param password 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		throw new SystemException("不允许修改认证状态");
	}

	@Override
	public String toString() {
		return EntityUtils.toString(this);
	}
}