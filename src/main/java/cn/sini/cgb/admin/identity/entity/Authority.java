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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.Session;
import org.springframework.security.core.GrantedAuthority;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 权限实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = Authority.TABLE_NAME)
public class Authority extends AbstractEntity implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_AUTHORITY";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 权限 */
	@Column(name = "AUTHORITY", unique = true, nullable = false)
	private String authority;

	/** 中文名称 */
	@Column(name = "NAME", nullable = false)
	private String name;

	/** 备注 */
	@Column(name = "REMARK")
	private String remark;

	/** 排序权重 */
	@Column(name = "SORT_WEIGHT", nullable = false)
	private Long sortWeight;

	/** 所属上一级权限 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SUPER_AUTHORITY")
	private Authority superAuthority;

	/** 子权限集合 */
	@OneToMany(mappedBy = "superAuthority", fetch = FetchType.LAZY)
	private Set<Authority> subAuthoritys = new HashSet<Authority>();

	/** 拥有该权限的角色集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_AUTHORITY_ROLE", joinColumns = @JoinColumn(name = "FK_AUTHORITY"), inverseJoinColumns = @JoinColumn(name = "FK_ROLE"))
	private Set<Role> roles = new HashSet<Role>();

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
	 * 获取权限
	 * 
	 * @return authority 权限
	 */
	public String getAuthority() {
		return authority;
	}

	/**
	 * 设置权限
	 * 
	 * @param authority 权限
	 */
	public void setAuthority(String authority) {
		this.authority = authority;
	}

	/**
	 * 获取中文名称
	 * 
	 * @return name 中文名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置中文名称
	 * 
	 * @param name 中文名称
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
	 * 获取排序权重
	 * 
	 * @return sortWeight 排序权重
	 */
	public Long getSortWeight() {
		return sortWeight;
	}

	/**
	 * 设置排序权重
	 * 
	 * @param sortWeight 排序权重
	 */
	public void setSortWeight(Long sortWeight) {
		this.sortWeight = sortWeight;
	}

	/**
	 * 获取所属上一级权限
	 * 
	 * @return superAuthority 所属上一级权限
	 */
	public Authority getSuperAuthority() {
		return superAuthority;
	}

	/**
	 * 设置所属上一级权限
	 * 
	 * @param superAuthority 所属上一级权限
	 */
	public void setSuperAuthority(Authority superAuthority) {
		this.superAuthority = superAuthority;
	}

	/**
	 * 获取子权限集合
	 * 
	 * @return subAuthoritys 子权限集合
	 */
	public Set<Authority> getSubAuthoritys() {
		return subAuthoritys;
	}

	/**
	 * 设置子权限集合
	 * 
	 * @param subAuthoritys 子权限集合
	 */
	public void setSubAuthoritys(Set<Authority> subAuthoritys) {
		this.subAuthoritys = subAuthoritys;
	}

	/**
	 * 获取拥有该权限的角色集合
	 * 
	 * @return roles 拥有该权限的角色集合
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * 设置拥有该权限的角色集合
	 * 
	 * @param roles 拥有该权限的角色集合
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public void remove(Session session) {
		for (Authority authority : this.subAuthoritys) {
			authority.remove(session);
		}
		super.remove(session);
	}
}