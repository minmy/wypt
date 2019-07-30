package cn.sini.cgb.api.cgb.entity.integral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 商品升级关系
 * @author 黎嘉权
 */
@Entity
@Table(name = UpgradeCommodities.TABLE_NAME)
public class UpgradeCommodities extends AbstractLogicalRemoveEntity{
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_UPGRADE_COMMODITIES";

	@Override
	public Long getId() {
		return id;
	}
	
	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;
	
	/** openid */
	@Column(name = "OPEN_ID")
	private String openId;
	
	/** 团单号 */
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	/** 订单号 */
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	/**
	 * 升级后的商品ID
	 */
	@Column(name = "NEW_COMMODITY_ID")
	private Long newCommodityId;
	
	/**
	 * 升级前的商品ID
	 */
	@Column(name = "OLD_COMMODITY_ID")
	private Long oldCommodityId;
	
	/** 积分流水号 */
	@Column(name = "INTEGRAL_NUMBER", unique = true, nullable = false)
	private String integralNumber;

	public Long getNewCommodityId() {
		return newCommodityId;
	}

	public void setNewCommodityId(Long newCommodityId) {
		this.newCommodityId = newCommodityId;
	}

	public Long getOldCommodityId() {
		return oldCommodityId;
	}

	public void setOldCommodityId(Long oldCommodityId) {
		this.oldCommodityId = oldCommodityId;
	}

	public String getIntegralNumber() {
		return integralNumber;
	}

	public void setIntegralNumber(String integralNumber) {
		this.integralNumber = integralNumber;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	} 

}
