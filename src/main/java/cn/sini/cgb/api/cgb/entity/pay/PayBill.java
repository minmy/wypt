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
 * 支付记录
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = PayBill.TABLE_NAME)
public class PayBill extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_PAYBILL";

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * 支付状态 DZF 待支付 ZFZ 支付中 YZF 已支付
	 * */
	public enum PayStatusEnum {

		DZF("待支付"),

		ZFZ("支付中"),

		YZF("已支付"),
		
		ZFSB("支付失败");

		public String desc;

		private PayStatusEnum(String desc) {
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
	@Column(name = "ORDER_NUMBER", nullable = false)
	private String orderNumber;
	/** 微信支付成功流水号 */
	@Column(name = "TRANSACTION_ID")
	private String transactionId;
	/** 订单金额 */
	@Column(name = "TOTAL_FEE", precision = 8, scale = 2)
	private BigDecimal total_fee;
	/** 实际支付金额 其实这个字段没用的 运算对账过程 全部用 total_fee */
	@Column(name = "SETTLEMENT_TOTAL_FEE", precision = 8, scale = 2)
	private BigDecimal settlement_total_fee;
	/** 支付完成时间 */
	@Column(name = "TIME_END")
	private Date time_end;
	/** 支付状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "PAY_STATUS")
	private PayStatusEnum payStatus;
	/** 退款结果 */
	@Column(name = "PAY_XML", length = 4000)
	private String payXml;

	public String getPayXml() {
		return payXml;
	}

	public void setPayXml(String payXml) {
		this.payXml = payXml;
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

	public BigDecimal getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}

	public BigDecimal getSettlement_total_fee() {
		return settlement_total_fee;
	}

	public void setSettlement_total_fee(BigDecimal settlement_total_fee) {
		this.settlement_total_fee = settlement_total_fee;
	}

	public Date getTime_end() {
		return time_end;
	}

	public void setTime_end(Date time_end) {
		this.time_end = time_end;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public PayStatusEnum getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(PayStatusEnum payStatus) {
		this.payStatus = payStatus;
	}
}
