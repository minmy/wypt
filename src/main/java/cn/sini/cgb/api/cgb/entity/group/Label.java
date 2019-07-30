package cn.sini.cgb.api.cgb.entity.group;

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

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 标签实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = Label.TABLE_NAME)
public class Label extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_LABEL";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 标签 */
	@Column(name = "TAG")
	private String tag;

	/** 说明 */
	@Column(name = "DESC_")
	private String desc_;

	/** 排序权重 */
	@Column(name = "SORT_")
	private Long sort;

	/** 所属上一级标签 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_LABEL")
	private Label superLabel;

	/** 子标签集合 */
	@OneToMany(mappedBy = "superLabel", fetch = FetchType.LAZY)
	private Set<Label> subLabels = new HashSet<Label>();

	/** 团单集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_LABEL_GROUPORDER", joinColumns = @JoinColumn(name = "FK_LABEL"), inverseJoinColumns = @JoinColumn(name = "FK_GROUP_ORDER"))
	private Set<GroupOrder> groupOrders = new HashSet<GroupOrder>();

	/**
	 * 获取团单集合
	 * 
	 * @return groupOrders团单集合
	 */
	public Set<GroupOrder> getGroupOrders() {
		return groupOrders;
	}

	/**
	 * 设置团单集合
	 * 
	 * @param groupOrders 团单集合
	 */
	public void setGroupOrders(Set<GroupOrder> groupOrders) {
		this.groupOrders = groupOrders;
	}

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
	 * 获取标签
	 * 
	 * @return tag标签
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 设置标签
	 * 
	 * @param tag 标签
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 获取说明
	 * 
	 * @return desc_说明
	 */
	public String getDesc_() {
		return desc_;
	}

	/**
	 * 设置说明
	 * 
	 * @param desc_ 说明
	 */
	public void setDesc_(String desc_) {
		this.desc_ = desc_;
	}

	/**
	 * 获取所属上一级标签
	 * 
	 * @return superLabel所属上一级标签
	 */
	public Label getSuperLabel() {
		return superLabel;
	}

	/**
	 * 设置所属上一级标签
	 * 
	 * @param superLabel 所属上一级标签
	 */
	public void setSuperLabel(Label superLabel) {
		this.superLabel = superLabel;
	}

	/**
	 * 获取子标签集合
	 * 
	 * @return subLabels子标签集合
	 */
	public Set<Label> getSubLabels() {
		return subLabels;
	}

	/**
	 * 设置子标签集合
	 * 
	 * @param subLabels 子标签集合
	 */
	public void setSubLabels(Set<Label> subLabels) {
		this.subLabels = subLabels;
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