package cn.sini.cgb.api.identity.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 接口角色实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiRole.TABLE_NAME)
public class ApiRole extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_ROLE";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 名称 */
	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	/** 微信用户集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_WECHAT_ROLE_USER", joinColumns = @JoinColumn(name = "FK_API_ROLE"), inverseJoinColumns = @JoinColumn(name = "FK_WECHAT_USER"))
	private Set<WeChatUser> weChatUsers = new HashSet<WeChatUser>();

	/** 接口资源集合 */
	@OrderBy("id")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_API_ROLE_RESOURCE", joinColumns = @JoinColumn(name = "FK_API_ROLE"), inverseJoinColumns = @JoinColumn(name = "FK_API_RESOURCE"))
	private Set<ApiResource> apiResources = new HashSet<ApiResource>();

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
	 * 获取名称
	 * 
	 * @return name 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取微信用户集合
	 * 
	 * @return weChatUsers 微信用户集合
	 */
	public Set<WeChatUser> getWeChatUsers() {
		return weChatUsers;
	}

	/**
	 * 设置微信用户集合
	 * 
	 * @param weChatUsers 微信用户集合
	 */
	public void setWeChatUsers(Set<WeChatUser> weChatUsers) {
		this.weChatUsers = weChatUsers;
	}

	/**
	 * 获取接口资源集合
	 * 
	 * @return apiResources 接口资源集合
	 */
	public Set<ApiResource> getApiResources() {
		return apiResources;
	}

	/**
	 * 设置接口资源集合
	 * 
	 * @param apiResources 接口资源集合
	 */
	public void setApiResources(Set<ApiResource> apiResources) {
		this.apiResources = apiResources;
	}
}