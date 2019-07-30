package cn.sini.cgb.api.cgb.entity.integral;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/*
 * 积分账户消费流水
 * @author 黎嘉权
 */
@Entity
@Table(name = IntegralAccountBill.TABLE_NAME)
public class IntegralAccountBill extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_INTEGRAL_ACCOUN_BILL";

	@Override
	public Long getId() {
		return id;
	}

	/** 积分来源类型 */
	public enum IntegralSourceTypeEnum {
		FX("分享") , TK("退款"), DH("兑换") ;

		public String desc;

		private IntegralSourceTypeEnum(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}
	@Enumerated(EnumType.STRING)
	@Column(name = "INTEGRAL_SOURCE_TYPE", nullable = false)
	private IntegralSourceTypeEnum integralSourceTypeEnum;
	/** 积分来源类型 */
	public IntegralSourceTypeEnum getIntegralSourceTypeEnum() {
		return integralSourceTypeEnum;
	}
	/** 积分来源类型 */
	public void setIntegralSourceTypeEnum(IntegralSourceTypeEnum integralSourceTypeEnum) {
		this.integralSourceTypeEnum = integralSourceTypeEnum;
	}

	/** 账户加减操作类型 */
	public enum IntegralTypeEnum {
		ZJ("增加"), JS("减少"), XF("消费");

		public String desc;

		private IntegralTypeEnum(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "INTEGRAL_TYPE", nullable = false)
	private IntegralTypeEnum integralType;
	
	/** 账户加减操作类型 */
	public IntegralTypeEnum getIntegralType() {
		return integralType;
	}
	/** 账户加减操作类型 */
	public void setIntegralType(IntegralTypeEnum integralType) {
		this.integralType = integralType;
	}
	
	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 被分享人的openid */
	@Column(name = "AFTER_OPENID")
	private String afterOpenId;
	
	/** openid */
	@Column(name = "OPEN_ID")
	private String openId;
	
	/**
	 * 消费积分
	 */
	@Column(name = "CONSUMPTION_INTEGRAL")
	private Long consumptionIntegral;
	/**
	 * 消费前积分
	 */
	@Column(name = "BEFORE_INTEGRAL")
	private Long beforeIntegral;
	
	/**
	 * 有效截止日期
	 */
	@Column(name = "VALID_DATE")
	private Date validDate;
	

	/** 团单号 */
	@Column(name = "GROUP_ID")
	private Long groupId;
	
	/** 订单号 */
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	
	/** 积分流水号 */
	@Column(name = "INTEGRAL_NUMBER", unique = true, nullable = false)
	private String integralNumber; 
	
	/** 商品编号 */
	@Column(name = "GROUP_COMMODITY_ID")
	private Long groupCommodityId;
	
	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 消费积分
	 */
	public Long getConsumptionIntegral() {
		return consumptionIntegral;
	}
	/**
	 * 消费积分
	 */
	public void setConsumptionIntegral(Long consumptionIntegral) {
		this.consumptionIntegral = consumptionIntegral;
	}
	/**
	 * 消费前积分
	 */
	public Long getBeforeIntegral() {
		return beforeIntegral;
	}
	/**
	 * 消费前积分
	 */
	public void setBeforeIntegral(Long beforeIntegral) {
		this.beforeIntegral = beforeIntegral;
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
	public Long getGroupCommodityId() {
		return groupCommodityId;
	}
	public void setGroupCommodityId(Long groupCommodityId) {
		this.groupCommodityId = groupCommodityId;
	}
	/**
	 * 有效截止日期
	 */
	public Date getValidDate() {
		return validDate;
	}
	/**
	 * 有效截止日期
	 */
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}
	public String getIntegralNumber() {
		return integralNumber;
	}
	public void setIntegralNumber(String integralNumber) {
		this.integralNumber = integralNumber;
	}
	/** 被分享人的openid */
	public String getAfterOpenId() {
		return afterOpenId;
	}
	/** 被分享人的openid */
	public void setAfterOpenId(String afterOpenId) {
		this.afterOpenId = afterOpenId;
	}
		
	
}
