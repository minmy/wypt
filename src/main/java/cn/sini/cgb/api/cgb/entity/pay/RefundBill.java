package cn.sini.cgb.api.cgb.entity.pay;

import java.math.BigDecimal;
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

/**
 * 退款记录
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = RefundBill.TABLE_NAME)
public class RefundBill extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_REFUNDBILL";

	/**
	 * 退款状态 DTK 待退款 TKZ 退款中 YTK 已退款
	 * */
	public enum RefundStatusEnum {
		/*
		 * 待退款
		 */
		DTK("待退款"), // 待退款

		/*
		 * 退款中
		 */
		TKZ("退款中"), // 退款中

		/*
		 * 已退款
		 */
		YTK("已退款"), // 已退款

		TKSB("退款失败");

		public String desc;

		private RefundStatusEnum(String desc) {
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
	/** openid */
	@Column(name = "OPEN_ID")
	private String openId;
	/** 业务订单号 */
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	/** 微信支付成功流水号 */
	@Column(name = "TRANSACTION_ID")
	private String transactionId;
	/** 业务退款单号 */
	@Column(name = "OUT_REFUND_NO")
	private String outRefundNo;
	/** 退款流水 */
	@Column(name = "REFUND_NO")
	private String refundNo;
	/** 申请退款金额 */
	@Column(name = "REFUND_FEE", precision = 8, scale = 2)
	private BigDecimal refundFee;
	/** 实际退款金额 */
	@Column(name = "SETTLEMENT_REFUND_FEE", precision = 8, scale = 2)
	private BigDecimal settlementRefundFee;
	/** 申请退款时间 */
	@Column(name = "REFUND_TIME")
	private Date refundTime;
	/** 实际退款时间 */
	@Column(name = "REAL_REFUND_TIME")
	private Date realRefundTime;
	/** 退款结果 */
	@Column(name = "REFUND_XML", length = 4000)
	private String refundXml;
	/** 退款状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "REFUND_STATUS")
	private RefundStatusEnum refundStatus;
	/** 退款通知 */
	@Column(name = "REFUND_NOTIFY")
	private String refundNotify;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public RefundStatusEnum getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(RefundStatusEnum refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	public String getRefundNo() {
		return refundNo;
	}

	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}

	public BigDecimal getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(BigDecimal refundFee) {
		this.refundFee = refundFee;
	}

	public BigDecimal getSettlementRefundFee() {
		return settlementRefundFee;
	}

	public void setSettlementRefundFee(BigDecimal settlementRefundFee) {
		this.settlementRefundFee = settlementRefundFee;
	}

	public Date getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	public Date getRealRefundTime() {
		return realRefundTime;
	}

	public void setRealRefundTime(Date realRefundTime) {
		this.realRefundTime = realRefundTime;
	}

	public String getRefundXml() {
		return refundXml;
	}

	public void setRefundXml(String refundXml) {
		this.refundXml = refundXml;
	}

	public String getRefundNotify() {
		return refundNotify;
	}

	public void setRefundNotify(String refundNotify) {
		this.refundNotify = refundNotify;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
