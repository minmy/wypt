package cn.sini.cgb.api.cgb.entity.pay;

import java.math.BigDecimal;
import java.util.Date;

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

import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 提现申请记录实体
 *
 * @author gaowei
 */
@Entity
@Table(name = ApplyWithdrawMoney.TABLE_NAME)
public class ApplyWithdrawMoney extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_APPLY_WITHDRAW_MONEY";

	/** 处理状态 */
	public enum HandleState {
		/** 待提现 */
		DTX("待提现"),
		/** 提现中 */
		TXZ("提现中"),
		/** 已提现 */
		YTX("已提现"),
		/** 提现失败 */
		TXSB("提现失败");

		public String desc;

		private HandleState(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	/** 审核状态 */
	public enum ExamineState {
		/** 未审核 */
		DSH("待审核"),
		/** 审核中 */
		SHZ("审核中"),
		/** 审核通过 */
		YTG("已通过"),
		/** 不通过 */
		BTG("不通过");

		public String desc;

		private ExamineState(String desc) {
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

	/** 提现申请编号 */
	@Column(name = "APPLY_NUMBER", nullable = false)
	private String applyNumber;

	/** 申请人OpenId */
	@Column(name = "OPENID", nullable = false)
	private String openId;

	/** *** */
	@Column(name = "SIGN")
	private String sign;

	/** *** */
	@Column(name = "TIME_STAMP")
	private String timestamp;

	/** 申请金额(单位：元) */
	@Column(name = "APPLY_MONEY_AMOUNT", precision = 8, scale = 2, nullable = false)
	private BigDecimal applyMoneyAmount;

	/** 实际金额(单位：元) */
	@Column(name = "ACTUAL_AMOUNT", precision = 8, scale = 2)
	private BigDecimal actualAmount;

	/** 处理状态 */
	@Column(name = "HANDLE_STATE", nullable = false)
	private HandleState handleState;

	/** 审核状态 */
	@Column(name = "EXAMINE_STATE")
	private ExamineState examineState;

	/** 处理完成时间（只有在处理状态变更为‘已处理’的时候更新该字段） */
	@Column(name = "HANDLE_COMPLETION_TIME")
	private Date HandleCompletionTime;

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 审核意见 */
	@Column(name = "AUDIT_OPINIONS")
	private String auditOpinions;

	/** 处理结果 */
	@Column(name = "TRANS_RESULTS")
	private String transResults;

	/** 微信付款单号 */
	@Column(name = "PAYMENT_NO")
	private String paymentNo;

	/** 微信付款时间 */
	@Column(name = "PAYMENT_TIME")
	private Date paymentTime;

	/** 重新提交次数 */
	@Column(name = "RETRY_NUMBER")
	private Integer retryNumber = 0;

	/** true=已发送，false=未发生 */
	@Column(name = "SENDEMAIL",columnDefinition = "bit(1) default 0")
	private Boolean sendEmail;

	/** 重新提交次数 */
	public Integer getRetryNumber() {
		return retryNumber;
	}
	/** 重新提交次数 */
	public void setRetryNumber(Integer retryNumber) {
		this.retryNumber = retryNumber;
	}
	/** 处理结果 */
	public String getTransResults() {
		return transResults;
	}
	/** 处理结果 */
	public void setTransResults(String transResults) {
		this.transResults = transResults;
	}
	/** 微信付款单号 */
	public String getPaymentNo() {
		return paymentNo;
	}
	/** 微信付款单号 */
	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}
	/** 微信付款时间 */
	public Date getPaymentTime() {
		return paymentTime;
	}
	/** 微信付款时间 */
	public void setPaymentTime(Date paymentTime) {
		this.paymentTime = paymentTime;
	}
	/** *** */
	public String getTimestamp() {
		return timestamp;
	}
	/** *** */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/** *** */
	public String getSign() {
		return sign;
	}
	/** *** */
	public void setSign(String sign) {
		this.sign = sign;
	}
	/** 审核状态 */
	public ExamineState getExamineState() {
		return examineState;
	}
	/** 审核状态 */
	public void setExamineState(ExamineState examineState) {
		this.examineState = examineState;
	}


	public String getAuditOpinions() {
		return auditOpinions;
	}
	public void setAuditOpinions(String auditOpinions) {
		this.auditOpinions = auditOpinions;
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
	 * 获取所属用户
	 *
	 * @return weChatUser所属用户
	 */
	public WeChatUser getWeChatUser() {
		return weChatUser;
	}

	/**
	 * 设置所属用户
	 *
	 * @param weChatUser 所属用户
	 */
	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	/**
	 * 获取申请人OpenId
	 *
	 * @return openId申请人OpenId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * 设置申请人OpenId
	 *
	 * @param openId 申请人OpenId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * 获取申请金额(单位：元)
	 *
	 * @return applyMoneyAmount申请金额(单位：元)
	 */
	public BigDecimal getApplyMoneyAmount() {
		return applyMoneyAmount;
	}

	/**
	 * 设置申请金额(单位：元)
	 *
	 * @param applyMoneyAmount 申请金额(单位：元)
	 */
	public void setApplyMoneyAmount(BigDecimal applyMoneyAmount) {
		this.applyMoneyAmount = applyMoneyAmount;
	}

	/**
	 * 获取处理状态
	 *
	 * @return handleState处理状态
	 */
	public HandleState getHandleState() {
		return handleState;
	}

	/**
	 * 设置处理状态
	 *
	 * @param handleState 处理状态
	 */
	public void setHandleState(HandleState handleState) {
		this.handleState = handleState;
	}

	/**
	 * 获取handleCompletionTime
	 *
	 * @return handleCompletionTimehandleCompletionTime
	 */
	public Date getHandleCompletionTime() {
		return HandleCompletionTime;
	}

	/**
	 * 设置handleCompletionTime
	 *
	 * @param handleCompletionTime handleCompletionTime
	 */
	public void setHandleCompletionTime(Date handleCompletionTime) {
		HandleCompletionTime = handleCompletionTime;
	}

	/**
	 * 获取实际金额(单位：元)
	 *
	 * @return actualAmount实际金额(单位：元)
	 */
	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	/**
	 * 设置实际金额(单位：元)
	 *
	 * @param actualAmount 实际金额(单位：元)
	 */
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}
