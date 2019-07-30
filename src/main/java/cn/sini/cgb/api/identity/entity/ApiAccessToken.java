package cn.sini.cgb.api.identity.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 接口访问令牌实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiAccessToken.TABLE_NAME)
public class ApiAccessToken extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_ACCESS_TOKEN";

	/** ID */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	/** 访问令牌 */
	@Column(name = "ACCESS_TOKEN", unique = true, nullable = false)
	private String accessToken = UUID.randomUUID().toString();

	/** 过期时间 */
	@Column(name = "EXPIRE_TIME", nullable = false)
	private Date expireTime;

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_USER", nullable = false)
	private User user;

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
	 * 获取访问令牌
	 * 
	 * @return accessToken 访问令牌
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * 设置访问令牌
	 * 
	 * @param accessToken 访问令牌
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return expireTime 过期时间
	 */
	public Date getExpireTime() {
		return expireTime;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param expireTime 过期时间
	 */
	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	/**
	 * 获取所属用户
	 * 
	 * @return user 所属用户
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 设置所属用户
	 * 
	 * @param user 所属用户
	 */
	public void setUser(User user) {
		this.user = user;
	}
}