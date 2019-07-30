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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 企业团收货方式
 * 
 * @author gaowei
 */
@Entity
@Table(name = WayOfDelivery.TABLE_NAME)
public class WayOfDelivery extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_WAY_OF_DELIVERY";

	/** 送货类型 */
	public enum DeliveryType {
		/** 自提 */
		ZT("自提"),
		/** 送货上门 */
		ZHSM("送货上门"),
		/** 核销 */
		HX("核销");

		public String desc;

		private DeliveryType(String desc) {
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

	/** 送货类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "DELIVERY_TYPE", nullable = false)
	private DeliveryType deliveryType;

	/** 描述值 */
	@Column(name = "DESC_")
	private String desc;

	/** 拥有的团单集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_GROUP_ORDER_WAY_OF_DELIVERY", joinColumns = @JoinColumn(name = "FK_WAY_OF_DELIVERY"), inverseJoinColumns = @JoinColumn(name = "FK_GROUP_ORDER"))
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
	 * 获取送货类型
	 * 
	 * @return deliveryType送货类型
	 */
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	/**
	 * 设置送货类型
	 * 
	 * @param deliveryType 送货类型
	 */
	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	/**
	 * 获取描述值
	 * 
	 * @return desc描述值
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 设置描述值
	 * 
	 * @param desc 描述值
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 获取拥有的团单集合
	 * 
	 * @return groupOrders拥有的团单集合
	 */
	public Set<GroupOrder> getGroupOrders() {
		return groupOrders;
	}

	/**
	 * 设置拥有的团单集合
	 * 
	 * @param groupOrders 拥有的团单集合
	 */
	public void setGroupOrders(Set<GroupOrder> groupOrders) {
		this.groupOrders = groupOrders;
	}
}
