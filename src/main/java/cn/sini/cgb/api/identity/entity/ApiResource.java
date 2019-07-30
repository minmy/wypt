package cn.sini.cgb.api.identity.entity;

import java.util.HashSet;
import java.util.Set;

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

import org.hibernate.Session;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 资源实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiResource.TABLE_NAME)
public class ApiResource extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_RESOURCE";

	/** 限制周期枚举 */
	public enum LimitPeriod {
		/** 分钟 */
		MINUTE("分钟"),
		/** 小时 */
		HOUR("小时"),
		/** 天 */
		DAY("天"),
		/** 月 */
		MONTH("月"),
		/** 年 */
		YEAR("年");

		private String desc;

		private LimitPeriod(String desc) {
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
	@Column(name = "ID")
	private Long id;

	/** 资源地址 */
	@Column(name = "URI", unique = true, nullable = false)
	private String uri;

	/** 资源名称 */
	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	/** 启用频率限制 */
	@Column(name = "ENABLE_RATE_LIMIT", nullable = false)
	private Boolean enableRateLimit;

	/** 限制周期 */
	@Enumerated(EnumType.STRING)
	@Column(name = "LIMIT_PERIOD", nullable = false)
	private LimitPeriod limitPeriod;

	/** 限制次数 */
	@Column(name = "LIMIT_COUNT", nullable = false)
	private Integer limitCount;

	/** 接口访问资源频率限制集合 */
	@OneToMany(mappedBy = "apiResource", fetch = FetchType.LAZY)
	private Set<ApiRateLimit> apiRateLimits = new HashSet<ApiRateLimit>();

	/** 需要的接口角色集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_API_ROLE_RESOURCE", joinColumns = @JoinColumn(name = "FK_API_RESOURCE"), inverseJoinColumns = @JoinColumn(name = "FK_API_ROLE"))
	private Set<ApiRole> apiRoles = new HashSet<ApiRole>();

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
	 * 获取资源地址
	 * 
	 * @return uri 资源地址
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 设置资源地址
	 * 
	 * @param uri 资源地址
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * 获取资源名称
	 * 
	 * @return name 资源名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置资源名称
	 * 
	 * @param name 资源名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取启用频率限制
	 * 
	 * @return enableRateLimit 启用频率限制
	 */
	public Boolean getEnableRateLimit() {
		return enableRateLimit;
	}

	/**
	 * 设置启用频率限制
	 * 
	 * @param enableRateLimit 启用频率限制
	 */
	public void setEnableRateLimit(Boolean enableRateLimit) {
		this.enableRateLimit = enableRateLimit;
	}

	/**
	 * 获取限制周期
	 * 
	 * @return limitPeriod 限制周期
	 */
	public LimitPeriod getLimitPeriod() {
		return limitPeriod;
	}

	/**
	 * 设置限制周期
	 * 
	 * @param limitPeriod 限制周期
	 */
	public void setLimitPeriod(LimitPeriod limitPeriod) {
		this.limitPeriod = limitPeriod;
	}

	/**
	 * 获取限制次数
	 * 
	 * @return limitCount 限制次数
	 */
	public Integer getLimitCount() {
		return limitCount;
	}

	/**
	 * 设置限制次数
	 * 
	 * @param limitCount 限制次数
	 */
	public void setLimitCount(Integer limitCount) {
		this.limitCount = limitCount;
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
	 * 获取需要的接口角色集合
	 * 
	 * @return apiRoles 需要的接口角色集合
	 */
	public Set<ApiRole> getApiRoles() {
		return apiRoles;
	}

	/**
	 * 设置需要的接口角色集合
	 * 
	 * @param apiRoles 需要的接口角色集合
	 */
	public void setApiRoles(Set<ApiRole> apiRoles) {
		this.apiRoles = apiRoles;
	}

	@Override
	public void remove(Session session) {
		for (ApiRateLimit apiRateLimit : this.apiRateLimits) {
			apiRateLimit.remove(session);
		}
		super.remove(session);
	}
}