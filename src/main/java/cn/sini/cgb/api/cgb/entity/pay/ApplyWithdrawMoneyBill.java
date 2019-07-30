package cn.sini.cgb.api.cgb.entity.pay;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 提现申请记录流水
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = ApplyWithdrawMoneyBill.TABLE_NAME)
public class ApplyWithdrawMoneyBill extends AbstractLogicalRemoveEntity{
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_APPLY_WITHDRAW_MONEY_BILL";
	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;
	@Override
	public Serializable getId() {
		return null;
	}
	/** 提现申请编号 */
	@Column(name = "APPLY_NUMBER", nullable = false)
	private String applyNumber;
	
	/** 申请人OpenId */
	@Column(name = "OPENID", nullable = false)
	private String openId;
	
	/** 处理结果 */
	@Column(name = "RETURN_CODE")
	private String returnCode;
	
	/** 处理结果 */
	@Column(name = "RESULT_CODE")
	private String resultCode;
	
	/** 处理结果 */
	@Column(name = "RETURN_MSG")
	private String returnMsg;	
	
	/** 处理结果,原文 */
	@Column(name = "TRANS_RESULTS_XML" ,length = 2000)
	private String transResultsXml;
	
	/** 结果 */
	@Column(name = "TRANS_RESULT" ,length = 2000)
	private String transResult;
	
	
	public String getTransResult() {
		return transResult;
	}

	public void setTransResult(String transResult) {
		this.transResult = transResult;
	}

	public String getApplyNumber() {
		return applyNumber;
	}

	public void setApplyNumber(String applyNumber) {
		this.applyNumber = applyNumber;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getTransResultsXml() {
		return transResultsXml;
	}

	public void setTransResultsXml(String transResultsXml) {
		this.transResultsXml = transResultsXml;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
