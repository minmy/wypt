package cn.sini.cgb.admin.identity.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.Session;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import cn.sini.cgb.admin.identity.query.AuthorityQuery;
import cn.sini.cgb.api.identity.entity.ApiAccessToken;
import cn.sini.cgb.api.identity.entity.ApiRateLimit;
import cn.sini.cgb.api.identity.entity.ApiRequestLimit;
import cn.sini.cgb.common.entity.AbstractEntity;
import cn.sini.cgb.common.util.DateTimeUtils;

/**
 * 用户实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = User.TABLE_NAME)
public class User extends AbstractEntity implements UserDetails {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_USER";
	public static final Integer ALLOW_LOGIN_ERROR_COUNT = 5;

	/** 用户类型枚举 */
	public enum UserType {
		/** 管理员 */
		ADMIN("管理员");

		private String desc;

		private UserType(String desc) {
			this.desc = desc;
		}

		/** 获取枚举的真实值 */
		public String getName() {
			return this.name();
		}

		/** 获取枚举的描述值 */
		public String getDesc() {
			return this.desc;
		}
	}

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	private Long id;

	/** 业务代码 */
	@Column(name = "SERVICE_CODE", unique = true, nullable = false)
	private String serviceCode = UUID.randomUUID().toString();

	/** 用户类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "USER_TYPE", nullable = false)
	private UserType userType;

	/** 帐号 */
	@Column(name = "USERNAME", unique = true, nullable = false)
	private String username;

	/** 密码 */
	@Column(name = "PASSWORD", nullable = false)
	private String password;

	/** 用户名称 */
	@Column(name = "FULLNAME", nullable = false)
	private String fullname;

	/** 帐号禁用 */
	@Column(name = "DISABLE", nullable = false)
	private Boolean disable = false;

	/** 前一次登录时间 */
	@Column(name = "PREV_LOGIN_TIME")
	private Date prevLoginTime;

	/** 最后登录时间 */
	@Column(name = "LAST_LOGIN_TIME")
	private Date lastLoginTime;

	/** 登录错误次数 */
	@Column(name = "LOGIN_ERROR_COUNT")
	private Integer loginErrorCount;

	/** 登录错误时间 */
	@Column(name = "LOGIN_ERROR_TIME")
	private Date loginErrorTime;

	/** email地址 */
	@Column(name = "EMAIL")
	private String email;

	/** 拥有的角色集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_USER_ROLE", joinColumns = @JoinColumn(name = "FK_USER"), inverseJoinColumns = @JoinColumn(name = "FK_ROLE"))
	private Set<Role> roles = new HashSet<Role>();

	/** 接口访问令牌集合 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<ApiAccessToken> apiAccessTokens = new HashSet<ApiAccessToken>();

	/** 接口访问资源频率限制集合 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<ApiRateLimit> apiRateLimits = new HashSet<ApiRateLimit>();

	/** 接口请求限制集合 */
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<ApiRequestLimit> apiRequestLimits = new HashSet<ApiRequestLimit>();

	/**
	 * 获取ID
	 * 
	 * @return id ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取业务代码
	 * 
	 * @return serviceCode 业务代码
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * 设置业务代码
	 * 
	 * @param serviceCode 业务代码
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * 获取用户类型
	 * 
	 * @return userType 用户类型
	 */
	public UserType getUserType() {
		return userType;
	}

	/**
	 * 设置用户类型
	 * 
	 * @param userType 用户类型
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
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

	/**
	 * 获取用户名称
	 * 
	 * @return fullname 用户名称
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * 设置用户名称
	 * 
	 * @param fullname 用户名称
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * 获取帐号禁用
	 * 
	 * @return disable 帐号禁用
	 */
	public Boolean getDisable() {
		return disable;
	}

	/**
	 * 设置帐号禁用
	 * 
	 * @param disable 帐号禁用
	 */
	public void setDisable(Boolean disable) {
		this.disable = disable;
	}

	/**
	 * 获取前一次登录时间
	 * 
	 * @return prevLoginTime 前一次登录时间
	 */
	public Date getPrevLoginTime() {
		return prevLoginTime;
	}

	/**
	 * 设置前一次登录时间
	 * 
	 * @param prevLoginTime 前一次登录时间
	 */
	public void setPrevLoginTime(Date prevLoginTime) {
		this.prevLoginTime = prevLoginTime;
	}

	/**
	 * 获取最后登录时间
	 * 
	 * @return lastLoginTime 最后登录时间
	 */
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * 设置最后登录时间
	 * 
	 * @param lastLoginTime 最后登录时间
	 */
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * 获取登录错误次数
	 * 
	 * @return loginErrorCount 登录错误次数
	 */
	public Integer getLoginErrorCount() {
		return loginErrorCount;
	}

	/**
	 * 设置登录错误次数
	 * 
	 * @param loginErrorCount 登录错误次数
	 */
	public void setLoginErrorCount(Integer loginErrorCount) {
		this.loginErrorCount = loginErrorCount;
	}

	/**
	 * 获取登录错误时间
	 * 
	 * @return loginErrorTime 登录错误时间
	 */
	public Date getLoginErrorTime() {
		return loginErrorTime;
	}

	/**
	 * 设置登录错误时间
	 * 
	 * @param loginErrorTime 登录错误时间
	 */
	public void setLoginErrorTime(Date loginErrorTime) {
		this.loginErrorTime = loginErrorTime;
	}

	/**
	 * 获取拥有的角色集合
	 * 
	 * @return roles 拥有的角色集合
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * 设置拥有的角色集合
	 * 
	 * @param roles 拥有的角色集合
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * 获取接口访问令牌集合
	 * 
	 * @return apiAccessTokens 接口访问令牌集合
	 */
	public Set<ApiAccessToken> getApiAccessTokens() {
		return apiAccessTokens;
	}

	/**
	 * 设置接口访问令牌集合
	 * 
	 * @param apiAccessTokens 接口访问令牌集合
	 */
	public void setApiAccessTokens(Set<ApiAccessToken> apiAccessTokens) {
		this.apiAccessTokens = apiAccessTokens;
	}

	/**
	 * 获取接口访问资源频率限制集合
	 * 
	 * @return apiRateLimits 接口访问资源频率限制集合
	 */
	public Set<ApiRateLimit> getApiRateLimits() {
		return apiRateLimits;
	}

	/**
	 * 设置接口访问资源频率限制集合
	 * 
	 * @param apiRateLimits 接口访问资源频率限制集合
	 */
	public void setApiRateLimits(Set<ApiRateLimit> apiRateLimits) {
		this.apiRateLimits = apiRateLimits;
	}

	/**
	 * 获取接口请求限制集合
	 * 
	 * @return apiRequestLimits 接口请求限制集合
	 */
	public Set<ApiRequestLimit> getApiRequestLimits() {
		return apiRequestLimits;
	}

	/**
	 * 设置接口请求限制集合
	 * 
	 * @param apiRequestLimits 接口请求限制集合
	 */
	public void setApiRequestLimits(Set<ApiRequestLimit> apiRequestLimits) {
		this.apiRequestLimits = apiRequestLimits;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Authority> authoritys = new HashSet<Authority>();
		for (Role role : this.roles) {
			authoritys.addAll(role.getAuthoritys());
		}
		return authoritys;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		int errorCount = this.loginErrorCount == null ? 0 : this.loginErrorCount;
		return !(errorCount >= ALLOW_LOGIN_ERROR_COUNT && DateTimeUtils.isSameDay(this.loginErrorTime, new Date()));
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return BooleanUtils.isNotTrue(this.disable);
	}

	@Override
	public void remove(Session session) {
		for (ApiAccessToken apiAccessToken : this.apiAccessTokens) {
			apiAccessToken.remove(session);
		}
		for (ApiRateLimit apiRateLimit : this.apiRateLimits) {
			apiRateLimit.remove(session);
		}
		for (ApiRequestLimit apiRequestLimit : this.apiRequestLimits) {
			apiRequestLimit.remove(session);
		}
		super.remove(session);
	}

	/** 是否为正确的密码 */
	public boolean isRealPassword(String password) {
		return getSha256Password(password, getServiceCode()).equals(this.password);
	}

	/** 加密密码 */
	public void encryptPassword() {
		this.password = getSha256Password(this.password, getServiceCode());
	}

	/** 获取SHA256算法的密码 */
	public static String getSha256Password(String password, String salt) {
		return DigestUtils.sha256Hex(password + "{" + salt.toString() + "}");
	}

	/** 是否有指定权限 */
	public boolean hasAuthority(String authorityString) {
		Authority authority = new AuthorityQuery().authority(authorityString).readOnly().uniqueResult();
		return this.getAuthorities().contains(authority);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}