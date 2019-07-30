package cn.sini.cgb.api.cgb.entity.group;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 镇区
 * 
 * @author gaowei
 */
@Entity
@Table(name = Township.TABLE_NAME)
public class Township extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_TOWNSHIP";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 镇区名称 */
	@Column(name = "NAME")
	private String name;

	/** 镇区编号 */
	@Column(name = "CODE_")
	private String code;

	/** 备注 */
	@Column(name = "REMARKS")
	private String remarks;

	/** 排序 */
	@Column(name = "SORT_")
	private Integer sort;

	/** 社区集合 */
	@OneToMany(mappedBy = "township", fetch = FetchType.LAZY)
	private Set<Community> communities = new HashSet<Community>();

	/**
	 * 获取ID
	 * 
	 * @return idID
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
	 * 获取name
	 * 
	 * @return namename
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置name
	 * 
	 * @param name name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取remarks
	 * 
	 * @return remarksremarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * 设置remarks
	 * 
	 * @param remarks remarks
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * 获取sort
	 * 
	 * @return sortsort
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * 设置sort
	 * 
	 * @param sort sort
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * 获取社区集合
	 * 
	 * @return communities社区集合
	 */
	public Set<Community> getCommunities() {
		return communities;
	}

	/**
	 * 设置社区集合
	 * 
	 * @param communities 社区集合
	 */
	public void setCommunities(Set<Community> communities) {
		this.communities = communities;
	}

	/**
	 * 获取镇区编号
	 * 
	 * @return code镇区编号
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置镇区编号
	 * 
	 * @param code 镇区编号
	 */
	public void setCode(String code) {
		this.code = code;
	}
}