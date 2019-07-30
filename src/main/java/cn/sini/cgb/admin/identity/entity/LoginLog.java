package cn.sini.cgb.admin.identity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 登录日志实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = LoginLog.TABLE_NAME)
public class LoginLog extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_LOGIN_LOG";

	/** 登录源枚举 */
	public enum LoginSource {
		/** PC管理端 */
		PC_MANAGE("PC管理端");

		private String desc;

		private LoginSource(String desc) {
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
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "ID")
	private String id;

	/** 登录源 */
	@Enumerated(EnumType.STRING)
	@Column(name = "LOGIN_SOURCE", nullable = false)
	private LoginSource loginSource;

	/** 用户代码 */
	@Column(name = "USER_CODE", nullable = false)
	private String userCode;

	/** 登录IP */
	@Column(name = "IP", nullable = false)
	private String ip;

	/**
	 * 获取ID
	 * 
	 * @return id ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取登录源
	 * 
	 * @return loginSource 登录源
	 */
	public LoginSource getLoginSource() {
		return loginSource;
	}

	/**
	 * 设置登录源
	 * 
	 * @param loginSource 登录源
	 */
	public void setLoginSource(LoginSource loginSource) {
		this.loginSource = loginSource;
	}

	/**
	 * 获取用户代码
	 * 
	 * @return userCode 用户代码
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * 设置用户代码
	 * 
	 * @param userCode 用户代码
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 * 获取登录IP
	 * 
	 * @return ip 登录IP
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 设置登录IP
	 * 
	 * @param ip 登录IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
}