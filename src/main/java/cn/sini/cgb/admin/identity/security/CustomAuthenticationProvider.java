package cn.sini.cgb.admin.identity.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import cn.sini.cgb.admin.identity.entity.LoginLog;
import cn.sini.cgb.admin.identity.entity.LoginLog.LoginSource;
import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.admin.identity.query.UserQuery;
import cn.sini.cgb.common.util.DateTimeUtils;

/**
 * 自定义认证服务提供者
 * 
 * @author 杨海彬
 */
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

	@Override
	@Transactional(noRollbackFor = { CustomAuthenticationException.class })
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			CustomAuthenticationToken token = (CustomAuthenticationToken) authentication;
			User user = retrieveUser(token);
			authenticationCheck(token, user);
			saveLoginLog(token, user);
			return new CustomAuthenticationToken(user, token);
		} catch (CustomAuthenticationException e) {
			throw e;
		} catch (Exception e) {
			LOGGER.error("登录认证异常，Token为[" + authentication + "]", e);
			throw new CustomAuthenticationException("登录失败，服务器内部错误，请稍后再试...", e);
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CustomAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/** 检索用户 */
	private User retrieveUser(CustomAuthenticationToken token) {
		User user = new UserQuery().username(token.getUsername()).uniqueResult();
		return user;
	}

	/** 身份认证检查 */
	private void authenticationCheck(CustomAuthenticationToken token, User user) {
		if (user == null) {
			throw new CustomAuthenticationException("登录失败，帐号或密码错误");
		}
		if (!user.isEnabled()) {
			throw new CustomAuthenticationException("登录失败，您的帐号已被禁用");
		}
		if (!user.isAccountNonExpired()) {
			throw new CustomAuthenticationException("登录失败，您的帐号已过期");
		}
		if (!user.isCredentialsNonExpired()) {
			throw new CustomAuthenticationException("登录失败，您的密码已过期");
		}
		if (!user.isAccountNonLocked()) {
			throw new CustomAuthenticationException("帐号已被锁定，请您明天再试或联系系统管理员解锁");
		}
		if (!user.hasAuthority("ROLE_VISIT_MANAGE")) {
			throw new CustomAuthenticationException("登录失败，您的帐号没有权限访问该系统");
		}
		if (!user.isRealPassword(token.getPassword())) {
			Integer loginErrorCount = user.getLoginErrorCount();
			Date loginErrorTime = user.getLoginErrorTime();
			Date currentTime = new Date();
			loginErrorCount = (loginErrorCount == null || !DateTimeUtils.isSameDay(loginErrorTime, currentTime)) ? 1 : (loginErrorCount + 1);
			user.setLoginErrorCount(loginErrorCount);
			user.setLoginErrorTime(currentTime);
			user.saveOrUpdate();
			if (loginErrorCount < User.ALLOW_LOGIN_ERROR_COUNT) {
				throw new CustomAuthenticationException("帐号或密码错误，您今天还可以尝试" + (User.ALLOW_LOGIN_ERROR_COUNT - loginErrorCount) + "次");
			} else {
				throw new CustomAuthenticationException("帐号已被锁定，请您明天再试或联系系统管理员解锁");
			}
		}
	}

	/** 保存登录日志 */
	private void saveLoginLog(CustomAuthenticationToken token, User user) {
		user.setLoginErrorCount(0);
		user.setPrevLoginTime(user.getLastLoginTime());
		user.setLastLoginTime(new Date());
		user.saveOrUpdate();
		LoginLog loginLog = new LoginLog();
		loginLog.setLoginSource(LoginSource.PC_MANAGE);
		loginLog.setUserCode(user.getServiceCode());
		loginLog.setIp(token.getLoginIp());
		loginLog.saveOrUpdate();
	}
}