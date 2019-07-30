package cn.sini.cgb.admin.identity.entity;

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

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 角色实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = Role.TABLE_NAME)
public class Role extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ROLE";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 名称 */
	@Column(name = "NAME", unique = true, nullable = false)
	private String name;

	/** 备注 */
	@Column(name = "REMARK")
	private String remark;

	/** 拥有的权限集合 */
	@OrderBy("sortWeight,id")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_AUTHORITY_ROLE", joinColumns = @JoinColumn(name = "FK_ROLE"), inverseJoinColumns = @JoinColumn(name = "FK_AUTHORITY"))
	private Set<Authority> authoritys = new HashSet<Authority>();

	/** 拥有该角色的用户集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_USER_ROLE", joinColumns = @JoinColumn(name = "FK_ROLE"), inverseJoinColumns = @JoinColumn(name = "FK_USER"))
	private Set<User> users = new HashSet<User>();

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
	 * 获取备注
	 * 
	 * @return remark 备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置备注
	 * 
	 * @param remark 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取拥有的权限集合
	 * 
	 * @return authoritys 拥有的权限集合
	 */
	public Set<Authority> getAuthoritys() {
		return authoritys;
	}

	/**
	 * 设置拥有的权限集合
	 * 
	 * @param authoritys 拥有的权限集合
	 */
	public void setAuthoritys(Set<Authority> authoritys) {
		this.authoritys = authoritys;
	}

	/**
	 * 获取拥有该角色的用户集合
	 * 
	 * @return users 拥有该角色的用户集合
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * 设置拥有该角色的用户集合
	 * 
	 * @param users 拥有该角色的用户集合
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}
}