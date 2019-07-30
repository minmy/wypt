package cn.sini.cgb.api.cgb.entity.integral;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/*
 * 积分账户
 * @author 黎嘉权
 */
@Entity
@Table(name = IntegralAccount.TABLE_NAME)
public class IntegralAccount extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_INTEGRAL_ACCOUN";

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
	@Column(name = "OPEN_ID" , unique = true , nullable = false)
	private String openId;
	
	/**
	 * 当前可用积分
	 */
	@Column(name = "CURRENT_INTEGRAL")
	private Long currentIntegral;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 当前可用积分
	 */
	public Long getCurrentIntegral() {
		return currentIntegral;
	}
	/**
	 * 当前可用积分
	 */
	public void setCurrentIntegral(Long currentIntegral) {
		this.currentIntegral = currentIntegral;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
}
