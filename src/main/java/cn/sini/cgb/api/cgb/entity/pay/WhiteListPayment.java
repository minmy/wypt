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
 * 提现白名单
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = WhiteListPayment.TABLE_NAME)
public class WhiteListPayment extends AbstractLogicalRemoveEntity{
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_WHITE_LIST_PAYMENT";
	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;
	
	/** 申请人OpenId */
	@Column(name = "OPENID", nullable = false , unique = true)
	private String openId;

	@Override
	public Serializable getId() {
		return null;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	
}
