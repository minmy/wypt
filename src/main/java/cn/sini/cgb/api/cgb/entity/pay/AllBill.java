package cn.sini.cgb.api.cgb.entity.pay;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * 支付/退款的通知
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = AllBill.TABLE_NAME)
public class AllBill extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ALLBILL";

	/** 账户操作类型 */
	public enum CashTypeEnum {
		KTX("可提现"), DTX("待提现"), YTX("已提现"), ZSR("总收入"), QX("取消");

		public String desc;

		private CashTypeEnum(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	/** 流水类型 */
	public enum BillTypeEnum {
		/** 收入 */
		SR("收入"),
		/** 退账 */
		TZ("退账");

		public String desc;

		private BillTypeEnum(String desc) {
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
	/** 所属团长 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;
	/** 通知类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "BILL_TYPE")
	private BillTypeEnum billType;
	/** 订单金额 */
	@Column(name = "TOTAL_FEE", precision = 12, scale = 2)
	private BigDecimal total_fee;
	/** 业务订单号 */
	@Column(name = "ORDER_NUMBER", nullable = false)
	private String orderNumber;
	/** 团单号 */
	@Column(name = "GROUP_ID", nullable = false)
	private String groupId;
	/** 提现单号 */
	@Column(name = "APPLY_NUMBER")
	private String applyNumber;
	/** 变更编号 用于发货定时任务 变更记录 退款溯源 */
	@Column(name = "CHANGE_NUMBER")
	private String changeNumber;
	/**
	 * 状态 0 = 不可提现 1 = 可提现
	 * */
	@Enumerated(EnumType.STRING)
	@Column(name = "FLAG", nullable = false)
	private CashTypeEnum flag;

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
	 * 获取所属团长
	 * 
	 * @return weChatUser所属团长
	 */
	public WeChatUser getWeChatUser() {
		return weChatUser;
	}

	/**
	 * 设置所属团长
	 * 
	 * @param weChatUser 所属团长
	 */
	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	/**
	 * 获取通知类型
	 * 
	 * @return billType通知类型
	 */
	public BillTypeEnum getBillType() {
		return billType;
	}

	/**
	 * 设置通知类型
	 * 
	 * @param billType 通知类型
	 */
	public void setBillType(BillTypeEnum billType) {
		this.billType = billType;
	}

	/**
	 * 获取订单金额
	 * 
	 * @return total_fee订单金额
	 */
	public BigDecimal getTotal_fee() {
		return total_fee;
	}

	/**
	 * 设置订单金额
	 * 
	 * @param total_fee 订单金额
	 */
	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}

	/**
	 * 获取业务订单号
	 * 
	 * @return orderNumber业务订单号
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * 设置业务订单号
	 * 
	 * @param orderNumber 业务订单号
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * 获取团单号
	 * 
	 * @return groupId团单号
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 设置团单号
	 * 
	 * @param groupId 团单号
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 获取提现单号
	 * 
	 * @return applyNumber提现单号
	 */
	public String getApplyNumber() {
		return applyNumber;
	}

	/**
	 * 设置提现单号
	 * 
	 * @param applyNumber 提现单号
	 */
	public void setApplyNumber(String applyNumber) {
		this.applyNumber = applyNumber;
	}

	/**
	 * 获取变更编号用于发货定时任务变更记录退款溯源
	 * 
	 * @return changeNumber变更编号用于发货定时任务变更记录退款溯源
	 */
	public String getChangeNumber() {
		return changeNumber;
	}

	/**
	 * 设置变更编号用于发货定时任务变更记录退款溯源
	 * 
	 * @param changeNumber 变更编号用于发货定时任务变更记录退款溯源
	 */
	public void setChangeNumber(String changeNumber) {
		this.changeNumber = changeNumber;
	}

	/**
	 * 获取状态0=不可提现1=可提现
	 * 
	 * @return flag状态0=不可提现1=可提现
	 */
	public CashTypeEnum getFlag() {
		return flag;
	}

	/**
	 * 设置状态0=不可提现1=可提现
	 * 
	 * @param flag 状态0=不可提现1=可提现
	 */
	public void setFlag(CashTypeEnum flag) {
		this.flag = flag;
	}
}
