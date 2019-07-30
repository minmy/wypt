package cn.sini.cgb.api.cgb.entity.pay;

import java.math.BigDecimal;

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
 * 虚拟账户流水表
 * 
 * @author gaowei
 */
@Entity
@Table(name = VirtualAccountBill.TABLE_NAME)
public class VirtualAccountBill extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_VIRTUAL_ACCOUNT_BILL";

	/** 账单类型 */
	public enum BillType {
		/**
		 * 到账
		 */
		DZ("到账"),
		/** 提现 */
		TX("提现"),
		/** 转入 */
		SR("收入"),
		/** 变更 */
		BG("变更"),
		/** 退款 */
		TK("退款");

		public String desc;

		private BillType(String desc) {
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

	/** openId */
	@Column(name = "OPEN_ID", nullable = false)
	private String openId;

	/** 提现申请编号 */
	@Column(name = "APPLY_NUMBER")
	private String applyNumber;
	
	/** 订单编号  用于收入 和 退款溯源*/
	@Column(name = "ORDER_NUMBER")
	private String orderNumber;
	
	/** 变更编号 用于发货定时任务 变更记录 退款溯源*/
	@Column(name = "CHANGE_NUMBER")
	private String changeNumber;

	/** 账单类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "BILL_TYPE", nullable = false)
	private BillType billType;

	/** 金额 */
	@Column(name = "AMOUNT_MONEY", precision = 12, scale = 2, nullable = false)
	private BigDecimal amountMoney;

	/** 操作前可提现金额 单位 元，但是微信需要的是 分，注意转换 */
	@Column(name = "BEFORE_WITHDRAWABLE_CASH", precision = 12, scale = 2, nullable = false)
	private BigDecimal beforeWithdrawableCash;

	/** 操作前不可提现金额 */
	@Column(name = "BEFORE_NOWITHDRAWN", precision = 12, scale = 2, nullable = false)
	private BigDecimal beforeNoWithdrawn;

	/** 操作前已提现金额 */
	@Column(name = "BEFORE_ALREADY_AVAILABLE", precision = 12, scale = 2, nullable = false)
	private BigDecimal beforeAlreadyAvailable;

	/** 操作前总收入 */
	@Column(name = "BEFORE_GROSS_INCOME", precision = 12, scale = 2, nullable = false)
	private BigDecimal beforeGrossIncome;

	/** 订单编号  用于收入 和 退款溯源*/
	public String getOrderNumber() {
		return orderNumber;
	}
	/** 订单编号  用于收入 和 退款溯源*/
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	/** 变更编号 用于发货定时任务 变更记录 退款溯源*/
	public String getChangeNumber() {
		return changeNumber;
	}
	/** 变更编号 用于发货定时任务 变更记录 退款溯源*/
	public void setChangeNumber(String changeNumber) {
		this.changeNumber = changeNumber;
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
	 * 获取openId
	 * 
	 * @return openIdopenId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * 设置openId
	 * 
	 * @param openId openId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * 获取提现申请编号
	 * 
	 * @return applyNumber提现申请编号
	 */
	public String getApplyNumber() {
		return applyNumber;
	}

	/**
	 * 设置提现申请编号
	 * 
	 * @param applyNumber 提现申请编号
	 */
	public void setApplyNumber(String applyNumber) {
		this.applyNumber = applyNumber;
	}

	/**
	 * 获取账单类型
	 * 
	 * @return billType账单类型
	 */
	public BillType getBillType() {
		return billType;
	}

	/**
	 * 设置账单类型
	 * 
	 * @param billType 账单类型
	 */
	public void setBillType(BillType billType) {
		this.billType = billType;
	}

	/**
	 * 获取金额
	 * 
	 * @return amountMoney金额
	 */
	public BigDecimal getAmountMoney() {
		return amountMoney;
	}

	/**
	 * 设置金额
	 * 
	 * @param amountMoney 金额
	 */
	public void setAmountMoney(BigDecimal amountMoney) {
		this.amountMoney = amountMoney;
	}

	/**
	 * 获取操作前可提现金额单位元，但是微信需要的是分，注意转换
	 * 
	 * @return beforeWithdrawableCash操作前可提现金额单位元，但是微信需要的是分，注意转换
	 */
	public BigDecimal getBeforeWithdrawableCash() {
		return beforeWithdrawableCash;
	}

	/**
	 * 设置操作前可提现金额单位元，但是微信需要的是分，注意转换
	 * 
	 * @param beforeWithdrawableCash 操作前可提现金额单位元，但是微信需要的是分，注意转换
	 */
	public void setBeforeWithdrawableCash(BigDecimal beforeWithdrawableCash) {
		this.beforeWithdrawableCash = beforeWithdrawableCash;
	}

	/**
	 * 获取操作前不可提现金额
	 * 
	 * @return beforeNoWithdrawn操作前不可提现金额
	 */
	public BigDecimal getBeforeNoWithdrawn() {
		return beforeNoWithdrawn;
	}

	/**
	 * 设置操作前不可提现金额
	 * 
	 * @param beforeNoWithdrawn 操作前不可提现金额
	 */
	public void setBeforeNoWithdrawn(BigDecimal beforeNoWithdrawn) {
		this.beforeNoWithdrawn = beforeNoWithdrawn;
	}

	/**
	 * 获取操作前已提现金额
	 * 
	 * @return beforeAlreadyAvailable操作前已提现金额
	 */
	public BigDecimal getBeforeAlreadyAvailable() {
		return beforeAlreadyAvailable;
	}

	/**
	 * 设置操作前已提现金额
	 * 
	 * @param beforeAlreadyAvailable 操作前已提现金额
	 */
	public void setBeforeAlreadyAvailable(BigDecimal beforeAlreadyAvailable) {
		this.beforeAlreadyAvailable = beforeAlreadyAvailable;
	}

	/**
	 * 获取操作前总收入
	 * 
	 * @return beforeGrossIncome操作前总收入
	 */
	public BigDecimal getBeforeGrossIncome() {
		return beforeGrossIncome;
	}

	/**
	 * 设置操作前总收入
	 * 
	 * @param beforeGrossIncome 操作前总收入
	 */
	public void setBeforeGrossIncome(BigDecimal beforeGrossIncome) {
		this.beforeGrossIncome = beforeGrossIncome;
	}
}
