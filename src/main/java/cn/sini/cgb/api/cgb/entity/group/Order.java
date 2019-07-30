package cn.sini.cgb.api.cgb.entity.group;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Where;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 订单实体
 *
 * @author gaowei
 */
@Entity
@Table(name = Order.TABLE_NAME)
public class Order extends AbstractLogicalRemoveEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_ORDER";

	/** 支付方式 */
	public enum PayMethod {
		微信;
	}

	/** 支付状态 */
	public enum PayState {
		/** 待支付 */
		DZF("待支付"),
		/** 支付中 */
		ZFZ("支付中"),
		/** 已支付 */
		YZF("已支付"),
		/** 支付失败 */
		YZSB("支付失败"),
		/** 待退款 */
		DTK("待退款"),
		/** 退款中 */
		TKZ("退款中"),
		/** 已退款 */
		YTK("已退款"),
		/** 退款失败 **/
		TKSB("退款失败");

		public String desc;

		private PayState(String desc) {
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

	/** 订单号 */
	@Column(name = "ORDER_NUMBER", unique = true, nullable = false)
	private String orderNumber;

	/** 手机号 */
	@Column(name = "PHONE")
	private String phone;

	/** 联系人 */
	@Column(name = "CONTACTS")
	private String contacts;

	/** 详细地址 */
	@Column(name = "ADDRESS")
	private String address;

	/** 备注 */
	@Column(name = "REMARKS")
	private String remarks;

	/** 订单商品(过滤掉已删除的订单商品) */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<OrderGoods> orderGoods = new HashSet<OrderGoods>();

	/** 订单状态 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_ORDER_STATE", nullable = false)
	private OrderState orderState;

	/** 总价 */
	@Column(name = "TOTAL", precision = 8, scale = 2)
	private BigDecimal total;

	/** 运费 */
	@Column(name = "FREIGHT", precision = 8, scale = 2)
	private BigDecimal freight = new BigDecimal(0);

	/** 优惠金额 */
	@Column(name = "DISCOUNT", precision = 8, scale = 2)
	private BigDecimal discount = new BigDecimal(0);

	/** 实际付款 */
	@Column(name = "FINAL_PAYMENT", precision = 8, scale = 2)
	private BigDecimal finalPayment;

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 支付方式 */
	@Enumerated(EnumType.STRING)
	@Column(name = "PAY_METHOD")
	private PayMethod payMethod;

	/** 支付流水号 */
	@Column(name = "PAY_NUMBER", unique = true)
	private String payNumber;

	/** 完成支付时间 */
	@Column(name = "PAY_TIME")
	private Date payTime;

	/** 支付状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "PAY_STATE")
	private PayState payState;

	/** 收货时间(完成时间) */
	@Column(name = "RECEIVING_TIME")
	private Date receivingTime;

	/** 取消时间 */
	@Column(name = "CANCEL_TIME")
	private Date cancelTime;

	/** 取消原因 */
	@Column(name = "CANCEL_REASON")
	private String cancelReason;

	/** 配送日期 */
	@Column(name = "DELIVERY_TIME")
	private Date deliveryTime;

	/** 关闭时间 */
	@Column(name = "CLOSING_TIME")
	private Date closingTime;

	/** 退款时间 */
	@Column(name = "REFUND_TIME")
	private Date refundTime;

	/** 所属团单 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER", nullable = false)
	private GroupOrder groupOrder;

	/** 退款流水 */
	@Column(name = "REFUND_NO")
	private String refundNo;

	/** 是否已恢复库存 */
	@Column(name = "IS_RECOVERY")
	private Boolean isRecovery = false;

	/** prepayId */
	@Column(name = "PREPAY_ID")
	private String prepayId;

	/** 统一支付时间 */
	@Column(name = "UINIONPAY_TIME")
	private Date uinionPaytime;

	/** 订单用户的提货照（团单发货后）（注意区分核销二维码的图片和用户上传的提货照，两者目前都与订单绑定） */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	@OrderBy("sort asc")
	private Set<Annex> annexs = new HashSet<Annex>();

	/** 分享人OPENID */
	@Column(name = "ORIGIN_OPENID")
	private String originOpenId;

	/** 分享人订单号 */
	@Column(name = "ORIGIN_ORDER_NUMBER")
	private String originOrderNumber;

	/** 积分流水号 */
	@Column(name = "INTEGRAL_NUMBER")
	private String integralNumber;

	/** 订单随机6位数(唯一) */
	@Column(name = "RANDOM_NUMBER", unique = true)
	private String randomNumber;

	/** 分享表唯一标识(唯一) */
	@Column(name = "SHARE_RANDOM_NUMBER")
	private String shareRandomNumber;

	/** 分享人订单号 */
	public String getOriginOrderNumber() {
		return originOrderNumber;
	}

	/** 分享人订单号 */
	public void setOriginOrderNumber(String originOrderNumber) {
		this.originOrderNumber = originOrderNumber;
	}

	/**
	 * 获取订单随机6位数
	 *
	 * @return randomNumber订单随机6位数
	 */
	public String getRandomNumber() {
		return randomNumber;
	}

	/**
	 * 设置订单随机6位数
	 *
	 * @param randomNumber 订单随机6位数
	 */
	public void setRandomNumber(String randomNumber) {
		this.randomNumber = randomNumber;
	}

	/** 分享人OPENID */
	public String getOriginOpenId() {
		return originOpenId;
	}

	/** 分享人OPENID */
	public void setOriginOpenId(String originOpenId) {
		this.originOpenId = originOpenId;
	}

	/**
	 * 获取统一支付时间
	 *
	 * @return uinionPaytime统一支付时间
	 */
	public Date getUinionPaytime() {
		return uinionPaytime;
	}

	/**
	 * 设置统一支付时间
	 *
	 * @param uinionPaytime 统一支付时间
	 */
	public void setUinionPaytime(Date uinionPaytime) {
		this.uinionPaytime = uinionPaytime;
	}

	/**
	 * 获取订单用户的提货照（团单发货后）
	 *
	 * @return annexs订单用户的提货照（团单发货后）
	 */
	public Set<Annex> getAnnexs() {
		return annexs;
	}

	/**
	 * 设置订单用户的提货照（团单发货后）
	 *
	 * @param annexs 订单用户的提货照（团单发货后）
	 */
	public void setAnnexs(Set<Annex> annexs) {
		this.annexs = annexs;
	}

	/**
	 * 获取prepayId
	 *
	 * @return prepayIdprepayId
	 */
	public String getPrepayId() {
		return prepayId;
	}

	/**
	 * 设置prepayId
	 *
	 * @param prepayId prepayId
	 */
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}

	/**
	 * 获取退款流水
	 *
	 * @return refundNo退款流水
	 */
	public String getRefundNo() {
		return refundNo;
	}

	/**
	 * 设置退款流水
	 *
	 * @param refundNo 退款流水
	 */
	public void setRefundNo(String refundNo) {
		this.refundNo = refundNo;
	}

	/**
	 * 获取是否已恢复库存
	 *
	 * @return isRecovery是否已恢复库存
	 */
	public Boolean getIsRecovery() {
		return isRecovery;
	}

	/**
	 * 设置是否已恢复库存
	 *
	 * @param isRecovery 是否已恢复库存
	 */
	public void setIsRecovery(Boolean isRecovery) {
		this.isRecovery = isRecovery;
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
	 * 获取订单号
	 *
	 * @return orderNumber订单号
	 */
	public String getOrderNumber() {
		return orderNumber;
	}

	/**
	 * 设置订单号
	 *
	 * @param orderNumber 订单号
	 */
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * 获取手机号
	 *
	 * @return phone手机号
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置手机号
	 *
	 * @param phone 手机号
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取联系人
	 *
	 * @return contacts联系人
	 */
	public String getContacts() {
		return contacts;
	}

	/**
	 * 设置联系人
	 *
	 * @param contacts 联系人
	 */
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	/**
	 * 获取详细地址
	 *
	 * @return address详细地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 设置详细地址
	 *
	 * @param address 详细地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取备注
	 *
	 * @return remarks备注
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * 设置备注
	 *
	 * @param remarks 备注
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * 获取订单商品(过滤掉已删除的订单商品)
	 *
	 * @return orderGoods订单商品(过滤掉已删除的订单商品)
	 */
	public Set<OrderGoods> getOrderGoods() {
		return orderGoods;
	}

	/**
	 * 设置订单商品(过滤掉已删除的订单商品)
	 *
	 * @param orderGoods 订单商品(过滤掉已删除的订单商品)
	 */
	public void setOrderGoods(Set<OrderGoods> orderGoods) {
		this.orderGoods = orderGoods;
	}

	/**
	 * 获取订单状态
	 *
	 * @return orderState订单状态
	 */
	public OrderState getOrderState() {
		return orderState;
	}

	/**
	 * 设置订单状态
	 *
	 * @param orderState 订单状态
	 */
	public void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	/**
	 * 获取总价
	 *
	 * @return total总价
	 */
	public BigDecimal getTotal() {
		return total;
	}

	/**
	 * 设置总价
	 *
	 * @param total 总价
	 */
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	/**
	 * 获取运费
	 *
	 * @return freight运费
	 */
	public BigDecimal getFreight() {
		return freight;
	}

	/**
	 * 设置运费
	 *
	 * @param freight 运费
	 */
	public void setFreight(BigDecimal freight) {
		this.freight = freight;
	}

	/**
	 * 获取优惠金额
	 *
	 * @return discount优惠金额
	 */
	public BigDecimal getDiscount() {
		return discount;
	}

	/**
	 * 设置优惠金额
	 *
	 * @param discount 优惠金额
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * 获取实际付款
	 *
	 * @return finalPayment实际付款
	 */
	public BigDecimal getFinalPayment() {
		return finalPayment;
	}

	/**
	 * 设置实际付款
	 *
	 * @param finalPayment 实际付款
	 */
	public void setFinalPayment(BigDecimal finalPayment) {
		this.finalPayment = finalPayment;
	}

	/** 积分流水号 */
	public String getIntegralNumber() {
		return integralNumber;
	}

	/** 积分流水号 */
	public void setIntegralNumber(String integralNumber) {
		this.integralNumber = integralNumber;
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
	 * 获取支付方式
	 *
	 * @return payMethod支付方式
	 */
	public PayMethod getPayMethod() {
		return payMethod;
	}

	/**
	 * 设置支付方式
	 *
	 * @param payMethod 支付方式
	 */
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	/**
	 * 获取支付流水号
	 *
	 * @return payNumber支付流水号
	 */
	public String getPayNumber() {
		return payNumber;
	}

	/**
	 * 设置支付流水号
	 *
	 * @param payNumber 支付流水号
	 */
	public void setPayNumber(String payNumber) {
		this.payNumber = payNumber;
	}

	/**
	 * 获取完成支付时间
	 *
	 * @return payTime支付时间
	 */
	public Date getPayTime() {
		return payTime;
	}

	/**
	 * 设置完成支付时间
	 *
	 * @param payTime 支付时间
	 */
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	/**
	 * 获取收货时间(完成时间)
	 *
	 * @return receivingTime收货时间(完成时间)
	 */
	public Date getReceivingTime() {
		return receivingTime;
	}

	/**
	 * 设置收货时间(完成时间)
	 *
	 * @param receivingTime 收货时间(完成时间)
	 */
	public void setReceivingTime(Date receivingTime) {
		this.receivingTime = receivingTime;
	}

	/**
	 * 获取取消时间
	 *
	 * @return cancelTime取消时间
	 */
	public Date getCancelTime() {
		return cancelTime;
	}

	/**
	 * 设置取消时间
	 *
	 * @param cancelTime 取消时间
	 */
	public void setCancelTime(Date cancelTime) {
		this.cancelTime = cancelTime;
	}

	/**
	 * 获取配送日期
	 *
	 * @return deliveryTime配送日期
	 */
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * 设置配送日期
	 *
	 * @param deliveryTime 配送日期
	 */
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	/**
	 * 获取关闭时间
	 *
	 * @return closingTime关闭时间
	 */
	public Date getClosingTime() {
		return closingTime;
	}

	/**
	 * 设置关闭时间
	 *
	 * @param closingTime 关闭时间
	 */
	public void setClosingTime(Date closingTime) {
		this.closingTime = closingTime;
	}

	/**
	 * 获取退款时间
	 *
	 * @return refundTime退款时间
	 */
	public Date getRefundTime() {
		return refundTime;
	}

	/**
	 * 设置退款时间
	 *
	 * @param refundTime 退款时间
	 */
	public void setRefundTime(Date refundTime) {
		this.refundTime = refundTime;
	}

	/**
	 * 获取所属团单
	 *
	 * @return groupOrder所属团单
	 */
	public GroupOrder getGroupOrder() {
		return groupOrder;
	}

	/**
	 * 设置所属团单
	 *
	 * @param groupOrder 所属团单
	 */
	public void setGroupOrder(GroupOrder groupOrder) {
		this.groupOrder = groupOrder;
	}

	/**
	 * 获取取消原因
	 *
	 * @return cancelReason取消原因
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * 设置取消原因
	 *
	 * @param cancelReason 取消原因
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * 获取支付状态
	 *
	 * @return payState支付状态
	 */
	public PayState getPayState() {
		return payState;
	}

	/**
	 * 设置支付状态
	 *
	 * @param payState 支付状态
	 */
	public void setPayState(PayState payState) {
		this.payState = payState;
	}

	public String getShareRandomNumber() {
		return shareRandomNumber;
	}

	public void setShareRandomNumber(String shareRandomNumber) {
		this.shareRandomNumber = shareRandomNumber;
	}

	/** 提货照上传时间 */
	public Date getPickPicTime() {
		Date i = null;

		for (Annex annex : this.getAnnexs()) {
			if (annex != null) {
				if (annex.getAnnexType() == AnnexType.ORDER_PICK_PIC) {
					i = annex.getCreateTime();
					break;
				}
			}
		}

		return i;
	}
}
