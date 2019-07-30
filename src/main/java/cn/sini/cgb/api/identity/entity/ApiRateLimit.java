package cn.sini.cgb.api.identity.entity;

import java.util.Date;

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
 * 接口访问资源频率限制实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiRateLimit.TABLE_NAME)
public class ApiRateLimit extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_RATE_LIMIT";

	/** ID */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	/** 周期起始时间 */
	@Column(name = "START_TIME", nullable = false)
	private Date startTime;

	/** 周期访问次数 */
	@Column(name = "ACCESS_COUNT", nullable = false)
	private Integer accessCount;

	/** 对应的用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_USER", nullable = false)
	private User user;

	/** 对应的接口资源 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_API_RESOURCE", nullable = false)
	private ApiResource apiResource;

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
	 * 获取周期起始时间
	 * 
	 * @return startTime 周期起始时间
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * 设置周期起始时间
	 * 
	 * @param startTime 周期起始时间
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * 获取周期访问次数
	 * 
	 * @return accessCount 周期访问次数
	 */
	public Integer getAccessCount() {
		return accessCount;
	}

	/**
	 * 设置周期访问次数
	 * 
	 * @param accessCount 周期访问次数
	 */
	public void setAccessCount(Integer accessCount) {
		this.accessCount = accessCount;
	}

	/**
	 * 获取对应的用户
	 * 
	 * @return user 对应的用户
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 设置对应的用户
	 * 
	 * @param user 对应的用户
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 获取对应的接口资源
	 * 
	 * @return apiResource 对应的接口资源
	 */
	public ApiResource getApiResource() {
		return apiResource;
	}

	/**
	 * 设置对应的接口资源
	 * 
	 * @param apiResource 对应的接口资源
	 */
	public void setApiResource(ApiResource apiResource) {
		this.apiResource = apiResource;
	}
}