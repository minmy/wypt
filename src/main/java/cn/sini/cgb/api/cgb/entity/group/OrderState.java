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

import org.hibernate.annotations.Where;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 订单状态实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = OrderState.TABLE_NAME)
public class OrderState extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ORDER_STATE";

	/** 订单状态 */
	public enum OrderStates {
		/** 待付款 */
		DFK("待付款"),
		/** 待收货 */
		DSH("待收货"),
		/** 已完成 */
		YWC("已完成"),
		/** 已取消 */
		YQX("已取消");

		public String desc;

		private OrderStates(String desc) {
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

	/** 订单状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "ORDER_STATES_", nullable = false)
	private OrderStates orderStates = OrderStates.DFK;

	/** 说明 */
	@Column(name = "DESC_", nullable = false)
	private String desc;

	/** 订单集合 */
	@OneToMany(mappedBy = "orderState", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Order> orders = new HashSet<Order>();

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
	 * 获取订单状态
	 * 
	 * @return orderStates订单状态
	 */
	public OrderStates getOrderStates() {
		return orderStates;
	}

	/**
	 * 设置订单状态
	 * 
	 * @param orderStates 订单状态
	 */
	public void setOrderStates(OrderStates orderStates) {
		this.orderStates = orderStates;
	}

	/**
	 * 获取desc
	 * 
	 * @return descdesc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 设置desc
	 * 
	 * @param desc desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 获取orders
	 * 
	 * @return ordersorders
	 */
	public Set<Order> getOrders() {
		return orders;
	}

	/**
	 * 设置orders
	 * 
	 * @param orders orders
	 */
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}
}
