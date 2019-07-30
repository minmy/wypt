package cn.sini.cgb.api.identity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 接口菜单实体
 * 
 * @author qi
 */
@Entity
@Table(name = ApiMenu.TABLE_NAME)
public class ApiMenu extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_MENU";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 名称 */
	@Column(name = "NAME", nullable = false)
	private String name;

	/** 地址 */
	@Column(name = "URL")
	private String url;

	/** 对应的角色id */
	@Column(name = "API_ROLE_ID", nullable = false)
	private Long apiRoleId;

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
	 * 获取地址
	 * 
	 * @return url 地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置地址
	 * 
	 * @param url 地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取对应的角色id
	 * 
	 * @return apiRoleId 对应的角色id
	 */
	public Long getApiRoleId() {
		return apiRoleId;
	}

	/**
	 * 设置对应的角色id
	 * 
	 * @param apiRoleId 对应的角色id
	 */
	public void setApiRoleId(Long apiRoleId) {
		this.apiRoleId = apiRoleId;
	}
}