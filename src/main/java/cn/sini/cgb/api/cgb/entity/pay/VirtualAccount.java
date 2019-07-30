package cn.sini.cgb.api.cgb.entity.pay;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 虚拟账户
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = VirtualAccount.TABLE_NAME)
public class VirtualAccount extends AbstractLogicalRemoveEntity{
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_VIRTUALACCOUNT";
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
	
	/**
	 * 可提现金额  单位 元，但是微信需要的是 分，注意转换，可直接向公司账户发起提现的金额
	 */
	@Column(name = "WITHDRAWABLE_CASH", precision = 12, scale = 2)
	private BigDecimal withdrawableCash;
	
	/**
	 * 不可提现金额  需要满足 T+1条件才可以提现，所有已支付的用户账单都会增加到这里
	 */
	@Column(name = "NO_WITHDRAWN", precision = 12, scale = 2)
	private BigDecimal noWithdrawn;
	
	/**
	 * already available
	 * 已提现金额 对账用
	 */
	@Column(name = "ALREADY_AVAILABLE", precision = 12, scale = 2)
	private BigDecimal alreadyAvailable;
	
	/**
	 * Gross income
	 * 总收入
	 */
	@Column(name = "GROSS_INCOME", precision = 12, scale = 2 )
	private BigDecimal grossIncome;
	

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 可提现金额  单位 元，但是微信需要的是 分，注意转换，可直接向公司账户发起提现的金额
	 */
	public BigDecimal getWithdrawableCash() {
		return withdrawableCash;
	}
	/**
	 * 可提现金额  单位 元，但是微信需要的是 分，注意转换，可直接向公司账户发起提现的金额
	 */
	public void setWithdrawableCash(BigDecimal withdrawableCash) {
		this.withdrawableCash = withdrawableCash;
	}
	/**
	 * 不可提现金额  需要满足 T+1条件才可以提现，所有已支付的用户账单都会增加到这里
	 */
	public BigDecimal getNoWithdrawn() {
		return noWithdrawn;
	}
	/**
	 * 不可提现金额  需要满足 T+1条件才可以提现，所有已支付的用户账单都会增加到这里
	 */
	public void setNoWithdrawn(BigDecimal noWithdrawn) {
		this.noWithdrawn = noWithdrawn;
	}
	/**
	 * already available
	 * 已提现金额 对账用
	 */
	public BigDecimal getAlreadyAvailable() {
		return alreadyAvailable;
	}
	/**
	 * already available
	 * 已提现金额 对账用
	 */
	public void setAlreadyAvailable(BigDecimal alreadyAvailable) {
		this.alreadyAvailable = alreadyAvailable;
	}
	/**
	 * Gross income
	 * 总收入
	 */
	public BigDecimal getGrossIncome() {
		return grossIncome;
	}
	/**
	 * Gross income
	 * 总收入
	 */
	public void setGrossIncome(BigDecimal grossIncome) {
		this.grossIncome = grossIncome;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
