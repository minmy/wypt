package cn.sini.cgb.api.cgb.entity.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 产品来源实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = ProductsSources.TABLE_NAME)
public class ProductsSources extends AbstractLogicalRemoveEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_PRODUCTS_SOURCES";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 名称 */
	@Column(name = "NAME")
	private String name;

	/** 备注 */
	@Column(name = "REMARKS")
	private String remarks;

	/** 排序权重 */
	@Column(name = "SORT_")
	private Long sort;

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
	 * 获取名称
	 * 
	 * @return name名称
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
	 * @return remarks备注
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * 设置备注
	 * 
	 * @param remarks 备注
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * 获取排序权重
	 * 
	 * @return sort排序权重
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * 设置排序权重
	 * 
	 * @param sort 排序权重
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}
}
