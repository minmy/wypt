package cn.sini.cgb.api.cgb.entity.group;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 订单商品实体（订单里面所订购的商品信息）
 * 
 * @author gaowei
 */
@Entity
@Table(name = OrderGoods.TABLE_NAME)
public class OrderGoods extends AbstractLogicalRemoveEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ORDER_GOODS";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 数量 */
	@Column(name = "AMOUNT", nullable = false)
	private Integer amount = 0;

	/** 总价 */
	@Column(name = "TOTAL", precision = 8, scale = 2)
	private BigDecimal total;

	/** 所属商品 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_COMMODITY", nullable = false)
	private GroupCommodity groupCommodity;

	/** 所属订单 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ORDER", nullable = false)
	private Order order;
	
	/** 是否通过 积分升级 为空 和 false 则没有升级，1 为升级商品 */
	@Column(name = "INTEGRAL_UPGRADE")
	private Boolean IntegralUpgrade = false;

	/** 是否通过 积分升级 为空 和 false 则没有升级，1 为升级商品 */
	public Boolean isIntegralUpgrade() {
		return IntegralUpgrade;
	}
	/** 是否通过 积分升级 为空 和 false 则没有升级，1 为升级商品 */
	public void setIntegralUpgrade(Boolean integralUpgrade) {
		IntegralUpgrade = integralUpgrade;
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
	 * 获取数量
	 * 
	 * @return amount数量
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * 设置数量
	 * 
	 * @param amount 数量
	 */
	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	/**
	 * 获取所属商品
	 * 
	 * @return groupCommodity所属商品
	 */
	public GroupCommodity getGroupCommodity() {
		return groupCommodity;
	}

	/**
	 * 设置所属商品
	 * 
	 * @param groupCommodity 所属商品
	 */
	public void setGroupCommodity(GroupCommodity groupCommodity) {
		this.groupCommodity = groupCommodity;
	}

	/**
	 * 获取所属订单
	 * 
	 * @return order所属订单
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 设置所属订单
	 * 
	 * @param order 所属订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取总价
	 * 
	 * @return total总价
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * 设置总价
	 * 
	 * @param total 总价
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
