package cn.sini.cgb.api.cgb.entity.group;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 团单状态实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = GroupOrderState.TABLE_NAME)
public class GroupOrderState extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_GROUP_ORDER_STATUS";

	/** 团单状态 */
	public enum States {
		/** 待发布 */
		DFB("待发布"),
		/** 进行中 */
		JXZ("进行中"),
		/** 未成团 */
		WCT("未成团"),
		/** 已结束 */
		YJS("已结束");

		public String desc;

		private States(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "STATES_", nullable = false)
	private States states;

	/** 状态描述值 */
	@Column(name = "DESC_", nullable = false)
	private String desc;

	/** 所属团单 */
	@OneToMany(mappedBy = "groupOrderState", fetch = FetchType.LAZY)
	private Set<GroupOrder> groupOrders = new HashSet<GroupOrder>();

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
	 * 获取状态
	 * 
	 * @return states状态
	 */
	public States getStates() {
		return states;
	}

	/**
	 * 设置状态
	 * 
	 * @param states 状态
	 */
	public void setStates(States states) {
		this.states = states;
	}

	/**
	 * 获取所属团单
	 * 
	 * @return groupOrders所属团单
	 */
	public Set<GroupOrder> getGroupOrders() {
		return groupOrders;
	}

	/**
	 * 设置所属团单
	 * 
	 * @param groupOrders 所属团单
	 */
	public void setGroupOrders(Set<GroupOrder> groupOrders) {
		this.groupOrders = groupOrders;
	}

	/**
	 * 获取状态描述值
	 * 
	 * @return desc状态描述值
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 设置状态描述值
	 * 
	 * @param desc 状态描述值
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
