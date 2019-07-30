package cn.sini.cgb.api.cgb.entity.verification;

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

import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 商家终端(商家白名单扫描器)实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = BusinessTerminal.TABLE_NAME)
public class BusinessTerminal extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_BUSINESS_TERMINAL";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 终端ID */
	@Column(name = "POS_ID", unique = true, nullable = false)
	private String posId;

	/** 所属商家 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_BUSINESS", nullable = false)
	private Business business;

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
	 * 获取终端ID
	 * 
	 * @return posId终端ID
	 */
	public String getPosId() {
		return posId;
	}

	/**
	 * 设置终端ID
	 * 
	 * @param posId 终端ID
	 */
	public void setPosId(String posId) {
		this.posId = posId;
	}

	/**
	 * 获取所属商家
	 * 
	 * @return business所属商家
	 */
	public Business getBusiness() {
		return business;
	}

	/**
	 * 设置所属商家
	 * 
	 * @param business 所属商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}
}
