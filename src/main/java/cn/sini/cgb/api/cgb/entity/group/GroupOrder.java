package cn.sini.cgb.api.cgb.entity.group;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.admin.annex.entity.Annex.AnnexType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.statistics.StaGroupOrder;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 团单实体
 *
 * @author 黎嘉权
 */
@Entity
@Table(name = GroupOrder.TABLE_NAME)
public class GroupOrder extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_GROUP_ORDER";

	/** 审核状态 */
	public enum ReviewStates {
		/** 待审核 */
		DSH("待审核"),
		/** 审核中 */
		SHZ("审核中"),
		/** 通过 */
		TG("通过"),
		/** 退回 */
		TH("退回");

		public String desc;

		private ReviewStates(String desc) {
			this.desc = desc;
		}

		/** 获取枚举的真实值 */
		public String getName() {
			return this.name();
		}

		/** 获取枚举的描述值 */
		public String getDesc() {
			return this.desc;
		}
	}

	/** 团单类型 */
	public enum GroupType {
		/** 普通团 */
		PTT("普通团"),
		/** 企业团 */
		QYT("企业团");

		public String desc;

		private GroupType(String desc) {
			this.desc = desc;
		}

		/** 获取枚举的真实值 */
		public String getName() {
			return this.name();
		}

		/** 获取枚举的描述值 */
		public String getDesc() {
			return this.desc;
		}
	}

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 主题 */
	@Column(name = "THEME", nullable = false)
	private String theme;

	/** 主题介绍 */
	@Column(name = "THEME_INTRODUCE")
	private String themeIntroduce;

	/** 拼团须知 */
	@Column(name = "NOTICE")
	private String notice;

	/** 开始时间 */
	@Column(name = "BEGIN_TIME")
	private Date beginTime;

	/** 结束时间 */
	@Column(name = "END_TIME")
	private Date endTime;

	/** 热度(团单的下单量, 不显示) */
	@Column(name = "HEAT_DEGREE")
	private Integer heatDegree = 0;

	/** 审核状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "REVIEW_STATES")
	private ReviewStates reviewStates;

	/** 审核时间 */
	@Column(name = "REVIEW_TIME")
	private Date reviewTime;

	/** 审批意见 */
	@Column(name = "REVIEW_REASON")
	private String reviewReason;

	/** 下限单数(暂时没用) */
	// @Column(name = "LOWER_LIMIT")
	// private Integer lowerLimit;

	/** 上限单数 */
	@Column(name = "UPPER_LIMIT")
	private Integer upperlimit = 999999;

	/** 发布时间 */
	@Column(name = "RELEASE_TIME")
	private Date releaseTime;

	/** 自提地址 */
	@Column(name = "SELF_EXTRACTING_ADDRESS")
	private String selfExtractingAddress;

	/** 自提时间 */
	@Column(name = "SELF_EXTRACTING_TIME")
	private Date selfExtractingTime;

	/** 自提结束时间 */
	@Column(name = "SELF_EXTRACTING_END_TIME")
	private Date selfExtractingEndTime;

	/** 取消时间 */
	@Column(name = "CANCEL_TIME")
	private Date cancelTime;

	/** 取消原因 */
	@Column(name = "CANCEL_REASON")
	private String cancelReason;

	/** 发货时间 */
	@Column(name = "DELIVERY_TIME")
	private Date deliveryTime;

	/** 可提现时间 */
	@Column(name = "CASH_WITHDRAWAL_TIME")
	private Date cashWithdrawalTime;

	/** 浏览量 */
	@Column(name = "BROWSE_VOLUME")
	private Integer browseVolume = 0;

	/** 团单状态 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER_STATE", nullable = false)
	private GroupOrderState groupOrderState;

	/** 团单图片 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@OrderBy("sort asc")
	private Set<Annex> annexs = new HashSet<Annex>();

	/** 团单商品（具体商品） */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	@OrderBy("sort asc")
	private Set<GroupCommodity> groupCommoditys = new HashSet<GroupCommodity>();

	/** 商品品目集合 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	@OrderBy("sort asc")
	private Set<GroupCommodityBasic> groupCommodityBasic = new HashSet<GroupCommodityBasic>();

	/** 团单访问记录 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<StaGroupOrder> staGroupOrders = new HashSet<StaGroupOrder>();

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 订单集合 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@OrderBy("createTime desc")
	private Set<Order> orders = new HashSet<Order>();

	/** 分享集合 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	private Set<Share> shares = new HashSet<Share>();

	/** 标签集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_LABEL_GROUPORDER", joinColumns = @JoinColumn(name = "FK_GROUP_ORDER"), inverseJoinColumns = @JoinColumn(name = "FK_LABEL"))
	private Set<Label> labels = new HashSet<Label>();

	/** 是否完成发货并结算金额 */
	@Column(name = "IS_FINISH")
	private Boolean isFinish = false;

	/** 所属小区 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_COMMUNITY")
	private Community community;

	/** 联系电话 */
	@Column(name = "PHONE")
	private String phone;

	/** 省 */
	@Column(name = "PROVINCE")
	private String province;

	/** 市 */
	@Column(name = "CITY")
	private String city;

	/** 镇区 */
	@Column(name = "TOWNSHIPS")
	private String townships;

	/** 街道 */
	@Column(name = "STREET")
	private String street;

	/** 等待时间 */
	@Column(name = "WAITING_TIME")
	private Integer waitingTime;

	/** 是否修改过发货时间 */
	@Column(name = "IS_DELIVERYTIME")
	private Boolean isDeliveryTime = false;

	/** 团单类型 */
	@Enumerated(EnumType.STRING)
	@Column(name = "GROUP_TYPE")
	private GroupType groupType;

	/** 是否组合商品（企业团）为true时，代表该团单的商品不能单独购买 */
	@Column(name = "IS_COMBINATION")
	private Boolean isCombination;

	/** 团单是否置顶 */
	@Column(name = "IS_TOP")
	private Boolean isTop = false;

	/** 是否可升级商品 */
	@Column(name = "IS_UPGRADE")
	private Boolean isUpgrade;

	/** 添加积分 */
	@Column(name = "ADD_INTEGRAL")
	private Integer addIntegral = 0;

	/** 拥有的收货方式集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_GROUP_ORDER_WAY_OF_DELIVERY", joinColumns = @JoinColumn(name = "FK_GROUP_ORDER"), inverseJoinColumns = @JoinColumn(name = "FK_WAY_OF_DELIVERY"))
	private Set<WayOfDelivery> wayOfDeliverys = new HashSet<WayOfDelivery>();

	/** 是否允许退款(true代表允许退，false不允许) */
	@Column(name = "IS_REFUND")
	private Boolean isRefund = true;

	/** 分享券(普通团), false代表关闭, true代表开启, 默认关闭 */
	@Column(name = "SHARE_COUPON")
	private Boolean shareCoupon = false;

	/** 分享数量(普通团), 用戶分享所获取的数量 */
	@Column(name = "SHARE_NUMBER")
	private Integer shareNumber = 0;

	/** 被分享数量(普通团), 被分享用戶所获取的分享券数量(点击分享所获取的分享券) */
	@Column(name = "INVITED_NUMBER")
	private Integer invitedNumber = 0;

	/** 成功邀请用户消费获取的分享券数量(普通团) */
	@Column(name = "SHARE_SUCCESS_NUMBER")
	private Integer shareSuccessNumber = 0;

	/** 用户通过邀请完成消费获得分享券数(普通团) */
	@Column(name = "SUCCESS_INVITED_NUMBER")
	private Integer successInvitedNumber;

	/** 分享优惠阀值(普通团) */
	@Column(name = "DISCOUNT_NUMBER")
	private Integer discountNumber = 0;

	/** 分享券使用说明(普通团) */
	@Column(name = "SHARE_COUPON_INSTRUCTIONS")
	private String shareCouponInstructions;

	/** 团单金额 */
	@Transient
	private BigDecimal groupPrice = new BigDecimal(0);

	/** 分享劵 */
	@OneToMany(mappedBy = "groupOrder", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<ShareTicket> shareTickets = new HashSet<>();

	/**
	 * 获取团单类型
	 *
	 * @return groupType团单类型
	 */
	public GroupType getGroupType() {
		return groupType;
	}

	/**
	 * 设置团单类型
	 *
	 * @param groupType 团单类型
	 */
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}

	/**
	 * 获取分享券(普通团)false代表关闭true代表开启默认关闭
	 *
	 * @return shareCoupon分享券(普通团)false代表关闭true代表开启默认关闭
	 */
	public Boolean getShareCoupon() {
		return shareCoupon;
	}

	/**
	 * 设置分享券(普通团)false代表关闭true代表开启默认关闭
	 *
	 * @param shareCoupon 分享券(普通团)false代表关闭true代表开启默认关闭
	 */
	public void setShareCoupon(Boolean shareCoupon) {
		this.shareCoupon = shareCoupon;
	}

	/**
	 * 获取分享数量(普通团)用戶分享所获取的数量
	 *
	 * @return shareNumber分享数量(普通团)用戶分享所获取的数量
	 */
	public Integer getShareNumber() {
		return shareNumber;
	}

	/**
	 * 设置分享数量(普通团)用戶分享所获取的数量
	 *
	 * @param shareNumber 分享数量(普通团)用戶分享所获取的数量
	 */
	public void setShareNumber(Integer shareNumber) {
		this.shareNumber = shareNumber;
	}

	/**
	 * 获取被分享数量(普通团)被分享用戶所获取的分享券数量(点击分享所获取的分享券)
	 *
	 * @return invitedNumber被分享数量(普通团)被分享用戶所获取的分享券数量(点击分享所获取的分享券)
	 */
	public Integer getInvitedNumber() {
		return invitedNumber;
	}

	/**
	 * 设置被分享数量(普通团)被分享用戶所获取的分享券数量(点击分享所获取的分享券)
	 *
	 * @param invitedNumber 被分享数量(普通团)被分享用戶所获取的分享券数量(点击分享所获取的分享券)
	 */
	public void setInvitedNumber(Integer invitedNumber) {
		this.invitedNumber = invitedNumber;
	}

	/**
	 * 获取成功邀请用户消费获取的分享券数量(普通团)
	 *
	 * @return shareSuccessNumber成功邀请用户消费获取的分享券数量(普通团)
	 */
	public Integer getShareSuccessNumber() {
		return shareSuccessNumber;
	}

	/**
	 * 设置成功邀请用户消费获取的分享券数量(普通团)
	 *
	 * @param shareSuccessNumber 成功邀请用户消费获取的分享券数量(普通团)
	 */
	public void setShareSuccessNumber(Integer shareSuccessNumber) {
		this.shareSuccessNumber = shareSuccessNumber;
	}

	/**
	 * 获取用户通过邀请完成消费获得分享券数(普通团)
	 *
	 * @return successInvitedNumber用户通过邀请完成消费获得分享券数(普通团)
	 */
	public Integer getSuccessInvitedNumber() {
		return successInvitedNumber;
	}

	/**
	 * 设置用户通过邀请完成消费获得分享券数(普通团)
	 *
	 * @param successInvitedNumber 用户通过邀请完成消费获得分享券数(普通团)
	 */
	public void setSuccessInvitedNumber(Integer successInvitedNumber) {
		this.successInvitedNumber = successInvitedNumber;
	}

	/**
	 * 获取分享优惠阀值(普通团)
	 *
	 * @return discountNumber分享优惠阀值(普通团)
	 */
	public Integer getDiscountNumber() {
		return discountNumber;
	}

	/**
	 * 设置分享优惠阀值(普通团)
	 *
	 * @param discountNumber 分享优惠阀值(普通团)
	 */
	public void setDiscountNumber(Integer discountNumber) {
		this.discountNumber = discountNumber;
	}

	/**
	 * 获取分享券使用说明(普通团)
	 *
	 * @return shareCouponInstructions分享券使用说明(普通团)
	 */
	public String getShareCouponInstructions() {
		return shareCouponInstructions;
	}

	/**
	 * 设置分享券使用说明(普通团)
	 *
	 * @param shareCouponInstructions 分享券使用说明(普通团)
	 */
	public void setShareCouponInstructions(String shareCouponInstructions) {
		this.shareCouponInstructions = shareCouponInstructions;
	}

	/**
	 * 获取添加积分
	 *
	 * @return addIntegral添加积分
	 */
	public Integer getAddIntegral() {
		return addIntegral;
	}

	/**
	 * 设置添加积分
	 *
	 * @param addIntegral 添加积分
	 */
	public void setAddIntegral(Integer addIntegral) {
		this.addIntegral = addIntegral;
	}

	/** 团单金额 */
	public BigDecimal getGroupPrice() {
		return groupPrice;
	}

	/** 团单金额 */
	public void setGroupPrice(BigDecimal groupPrice) {
		this.groupPrice = groupPrice;
	}

	/**
	 * 获取团单访问记录
	 *
	 * @return staGroupOrders团单访问记录
	 */
	public Set<StaGroupOrder> getStaGroupOrders() {
		return staGroupOrders;
	}

	/**
	 * 设置团单访问记录
	 *
	 * @param staGroupOrders 团单访问记录
	 */
	public void setStaGroupOrders(Set<StaGroupOrder> staGroupOrders) {
		this.staGroupOrders = staGroupOrders;
	}

	/**
	 * 获取商品品目集合
	 *
	 * @return groupCommodityBasic商品品目集合
	 */
	public Set<GroupCommodityBasic> getGroupCommodityBasic() {
		return groupCommodityBasic;
	}

	/**
	 * 设置商品品目集合
	 *
	 * @param groupCommodityBasic 商品品目集合
	 */
	public void setGroupCommodityBasic(Set<GroupCommodityBasic> groupCommodityBasic) {
		this.groupCommodityBasic = groupCommodityBasic;
	}

	/**
	 * 获取等待时间
	 *
	 * @return waitingTime等待时间
	 */
	public Integer getWaitingTime() {
		return waitingTime;
	}

	/** 是否修改过发货时间 */
	public Boolean getIsDeliveryTime() {
		return isDeliveryTime;
	}

	/** 是否修改过发货时间 */
	public void setIsDeliveryTime(Boolean isDeliveryTime) {
		this.isDeliveryTime = isDeliveryTime;
	}

	/**
	 * 设置等待时间
	 *
	 * @param waitingTime 等待时间
	 */
	public void setWaitingTime(Integer waitingTime) {
		this.waitingTime = waitingTime;
	}

	/**
	 * 获取是否完成发货并结算金额
	 *
	 * @return isFinish是否完成发货并结算金额
	 */
	public Boolean getIsFinish() {
		return isFinish;
	}

	/**
	 * 设置是否完成发货并结算金额
	 *
	 * @param isFinish 是否完成发货并结算金额
	 */
	public void setIsFinish(Boolean isFinish) {
		this.isFinish = isFinish;
	}

	/**
	 * 获取标签集合
	 *
	 * @return labels标签集合
	 */
	public Set<Label> getLabels() {
		return labels;
	}

	/**
	 * 设置标签集合
	 *
	 * @param labels 标签集合
	 */
	public void setLabels(Set<Label> labels) {
		this.labels = labels;
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
	 * 获取主题
	 *
	 * @return theme主题
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * 设置主题
	 *
	 * @param theme 主题
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * 获取主题介绍
	 *
	 * @return themeIntroduce主题介绍
	 */
	public String getThemeIntroduce() {
		return themeIntroduce;
	}

	/**
	 * 设置主题介绍
	 *
	 * @param themeIntroduce 主题介绍
	 */
	public void setThemeIntroduce(String themeIntroduce) {
		this.themeIntroduce = themeIntroduce;
	}

	/**
	 * 获取自提地址
	 *
	 * @return selfExtractingAddress自提地址
	 */
	public String getSelfExtractingAddress() {
		return selfExtractingAddress;
	}

	/**
	 * 设置自提地址
	 *
	 * @param selfExtractingAddress 自提地址
	 */
	public void setSelfExtractingAddress(String selfExtractingAddress) {
		this.selfExtractingAddress = selfExtractingAddress;
	}

	/**
	 * 获取自提时间
	 *
	 * @return selfExtractingTime自提时间
	 */
	public Date getSelfExtractingTime() {
		return selfExtractingTime;
	}

	/**
	 * 设置自提时间
	 *
	 * @param selfExtractingTime 自提时间
	 */
	public void setSelfExtractingTime(Date selfExtractingTime) {
		this.selfExtractingTime = selfExtractingTime;
	}

	/**
	 * 获取拼团须知
	 *
	 * @return notice拼团须知
	 */
	public String getNotice() {
		return notice;
	}

	/**
	 * 设置拼团须知
	 *
	 * @param notice 拼团须知
	 */
	public void setNotice(String notice) {
		this.notice = notice;
	}

	/**
	 * 获取开始时间
	 *
	 * @return beginTime开始时间
	 */
	public Date getBeginTime() {
		return beginTime;
	}

	/**
	 * 设置开始时间
	 *
	 * @param beginTime 开始时间
	 */
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * 获取结束时间
	 *
	 * @return endTime结束时间
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * 设置结束时间
	 *
	 * @param endTime 结束时间
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * 获取上限单数
	 *
	 * @return upperlimit上限单数
	 */
	public Integer getUpperlimit() {
		return upperlimit;
	}

	/**
	 * 设置上限单数
	 *
	 * @param upperlimit 上限单数
	 */
	public void setUpperlimit(Integer upperlimit) {
		this.upperlimit = upperlimit;
	}

	/**
	 * 获取团单图片
	 *
	 * @return annexs团单图片
	 */
	public Set<Annex> getAnnexs() {
		return annexs;
	}

	/**
	 * 设置团单图片
	 *
	 * @param annexs 团单图片
	 */
	public void setAnnexs(Set<Annex> annexs) {
		this.annexs = annexs;
	}

	/**
	 * 获取团单商品
	 *
	 * @return groupCommoditys团单商品
	 */
	public Set<GroupCommodity> getGroupCommoditys() {
		return groupCommoditys;
	}

	/**
	 * 设置团单商品
	 *
	 * @param groupCommoditys 团单商品
	 */
	public void setGroupCommoditys(Set<GroupCommodity> groupCommoditys) {
		this.groupCommoditys = groupCommoditys;
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
	 * 获取团单状态
	 *
	 * @return groupOrderState团单状态
	 */
	public GroupOrderState getGroupOrderState() {
		return groupOrderState;
	}

	/**
	 * 设置团单状态
	 *
	 * @param groupOrderState 团单状态
	 */
	public void setGroupOrderState(GroupOrderState groupOrderState) {
		this.groupOrderState = groupOrderState;
	}

	/**
	 * 获取订单集合
	 *
	 * @return orders订单集合
	 */
	public Set<Order> getOrders() {
		return orders;
	}

	/**
	 * 设置订单集合
	 *
	 * @param orders 订单集合
	 */
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取发布时间
	 *
	 * @return releaseTime发布时间
	 */
	public Date getReleaseTime() {
		return releaseTime;
	}

	/**
	 * 设置发布时间
	 *
	 * @param releaseTime 发布时间
	 */
	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
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
	 * 获取发货时间
	 *
	 * @return deliveryTime发货时间
	 */
	public Date getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * 设置发货时间
	 *
	 * @param deliveryTime 发货时间
	 */
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	/**
	 * 获取可提现时间
	 *
	 * @return cashWithdrawalTime可提现时间
	 */
	public Date getCashWithdrawalTime() {
		return cashWithdrawalTime;
	}

	/**
	 * 设置可提现时间
	 *
	 * @param cashWithdrawalTime 可提现时间
	 */
	public void setCashWithdrawalTime(Date cashWithdrawalTime) {
		this.cashWithdrawalTime = cashWithdrawalTime;
	}

	/**
	 * 获取分享集合
	 *
	 * @return shares分享集合
	 */
	public Set<Share> getShares() {
		return shares;
	}

	/**
	 * 设置分享集合
	 *
	 * @param shares 分享集合
	 */
	public void setShares(Set<Share> shares) {
		this.shares = shares;
	}

	/**
	 * 获取下限单数
	 *
	 * @return lowerLimit下限单数
	 */
	// public Integer getLowerLimit() {
	// return lowerLimit;
	// }

	/**
	 * 设置下限单数
	 *
	 * @param lowerLimit 下限单数
	 */
	// public void setLowerLimit(Integer lowerLimit) {
	// this.lowerLimit = lowerLimit;
	// }

	/**
	 * 获取热度(团单的下单量不显示)
	 *
	 * @return heatDegree热度(团单的下单量不显示)
	 */
	public Integer getHeatDegree() {
		return heatDegree;
	}

	/**
	 * 设置热度(团单的下单量不显示)
	 *
	 * @param heatDegree 热度(团单的下单量不显示)
	 */
	public void setHeatDegree(Integer heatDegree) {
		this.heatDegree = heatDegree;
	}

	/**
	 * 获取审核状态
	 *
	 * @return reviewStates 审核状态
	 */
	public ReviewStates getReviewStates() {
		return reviewStates;
	}

	/**
	 * 设置审核状态
	 *
	 * @param reviewStates 审核状态
	 */
	public void setReviewStates(ReviewStates reviewStates) {
		this.reviewStates = reviewStates;
	}

	/**
	 * 获取审核时间
	 *
	 * @return reviewTime 审核时间
	 */
	public Date getReviewTime() {
		return reviewTime;
	}

	/**
	 * 设置审核时间
	 *
	 * @param reviewTime 审核时间
	 */
	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	/**
	 * 获取审批意见
	 *
	 * @return reviewReason 审批意见
	 */
	public String getReviewReason() {
		return reviewReason;
	}

	/**
	 * 设置审批意见
	 *
	 * @param reviewReason 审批意见
	 */
	public void setReviewReason(String reviewReason) {
		this.reviewReason = reviewReason;
	}

	/**
	 * 获取浏览量
	 *
	 * @return browseVolume浏览量
	 */
	public Integer getBrowseVolume() {
		return browseVolume;
	}

	/**
	 * 设置浏览量
	 *
	 * @param browseVolume 浏览量
	 */
	public void setBrowseVolume(Integer browseVolume) {
		this.browseVolume = browseVolume;
	}

	/**
	 * 获取所属小区
	 *
	 * @return community所属小区
	 */
	public Community getCommunity() {
		return community;
	}

	/**
	 * 设置所属小区
	 *
	 * @param community 所属小区
	 */
	public void setCommunity(Community community) {
		this.community = community;
	}

	/**
	 * 获取联系电话
	 *
	 * @return phone联系电话
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置联系电话
	 *
	 * @param phone 联系电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取省
	 *
	 * @return province省
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * 设置省
	 *
	 * @param province 省
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * 获取市
	 *
	 * @return city市
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 设置市
	 *
	 * @param city 市
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * 获取镇区
	 *
	 * @return townships镇区
	 */
	public String getTownships() {
		return townships;
	}

	/**
	 * 设置镇区
	 *
	 * @param townships 镇区
	 */
	public void setTownships(String townships) {
		this.townships = townships;
	}

	/**
	 * 获取是否组合商品（企业团）为true时，代表该团单的商品不能单独购买
	 *
	 * @return isCombination是否组合商品（企业团）为true时，代表该团单的商品不能单独购买
	 */
	public Boolean getIsCombination() {
		return isCombination;
	}

	/**
	 * 设置是否组合商品（企业团）为true时，代表该团单的商品不能单独购买
	 *
	 * @param isCombination 是否组合商品（企业团）为true时，代表该团单的商品不能单独购买
	 */
	public void setIsCombination(Boolean isCombination) {
		this.isCombination = isCombination;
	}

	/**
	 * 获取团单是否置顶
	 *
	 * @return isTop团单是否置顶
	 */
	public Boolean getIsTop() {
		return isTop;
	}

	/**
	 * 设置团单是否置顶
	 *
	 * @param isTop 团单是否置顶
	 */
	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	/**
	 * 获取自提结束时间
	 *
	 * @return selfExtractingEndTime自提结束时间
	 */
	public Date getSelfExtractingEndTime() {
		return selfExtractingEndTime;
	}

	/**
	 * 设置自提结束时间
	 *
	 * @param selfExtractingEndTime 自提结束时间
	 */
	public void setSelfExtractingEndTime(Date selfExtractingEndTime) {
		this.selfExtractingEndTime = selfExtractingEndTime;
	}

	/**
	 * 获取街道
	 *
	 * @return street街道
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * 设置街道
	 *
	 * @param street 街道
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * 获取是否可升级商品
	 *
	 * @return isUpgrade是否可升级商品
	 */
	public Boolean getIsUpgrade() {
		return isUpgrade;
	}

	/**
	 * 设置是否可升级商品
	 *
	 * @param isUpgrade 是否可升级商品
	 */
	public void setIsUpgrade(Boolean isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	/**
	 * 获取拥有的收货方式集合
	 *
	 * @return wayOfDeliverys拥有的收货方式集合
	 */
	public Set<WayOfDelivery> getWayOfDeliverys() {
		return wayOfDeliverys;
	}

	/**
	 * 设置拥有的收货方式集合
	 *
	 * @param wayOfDeliverys 拥有的收货方式集合
	 */
	public void setWayOfDeliverys(Set<WayOfDelivery> wayOfDeliverys) {
		this.wayOfDeliverys = wayOfDeliverys;
	}

	/**
	 * 获取是否允许退款(true代表允许退，false不允许)
	 *
	 * @return isRefund是否允许退款(true代表允许退，false不允许)
	 */
	public Boolean getIsRefund() {
		return isRefund;
	}

	/**
	 * 设置是否允许退款(true代表允许退，false不允许)
	 *
	 * @param isRefund 是否允许退款(true代表允许退，false不允许)
	 */
	public void setIsRefund(Boolean isRefund) {
		this.isRefund = isRefund;
	}

	public Set<ShareTicket> getShareTickets() {
		return shareTickets;
	}

	public void setShareTickets(Set<ShareTicket> shareTickets) {
		this.shareTickets = shareTickets;
	}

	/** 判断普通团是否可以发布 */
	public Boolean isRelease() {
		long thisTime = System.currentTimeMillis();
		// 状态为 待发布, 主题不可为空, 主题介绍不可为空, 拼团须知不可为空, 开始时间不可为空, 结束时间不可为空並且要大于当前时间, 上限单数不可为空也不可为0, 自提地点不可为空, 自提时间不可为空并且要大于结束时间, 且电话号码不可为空, 且小区不可为空, 分享券不可为空
		if (this.getGroupOrderState().getStates() == States.DFB && StringUtils.isNotEmpty(this.getTheme()) && StringUtils.isNotEmpty(this.getThemeIntroduce()) && StringUtils.isNotEmpty(this.getNotice()) && this.getBeginTime() != null
				&& this.getEndTime() != null && this.getEndTime().getTime() > thisTime && this.getUpperlimit() != null && this.getUpperlimit() != 0 && StringUtils.isNotEmpty(this.getSelfExtractingAddress())
				&& this.getSelfExtractingTime() != null && this.getSelfExtractingTime().getTime() > this.getEndTime().getTime() && StringUtils.isNotEmpty(this.getPhone()) && this.weChatUser.getCommunity() != null
				&& this.shareCoupon != null) {
			// 当分享券为true时，获取对应的分享券数量不可为空
			if (this.shareCoupon) {
				if (this.shareNumber == null || this.invitedNumber == null || this.shareSuccessNumber == null || this.successInvitedNumber == null || this.discountNumber == null || StringUtils.isBlank(this.shareCouponInstructions)) {
					return false;
				}
			}
			Iterator<GroupCommodity> iterator = this.getGroupCommoditys().iterator();
			while (iterator.hasNext()) {
				GroupCommodity commodity = (GroupCommodity) iterator.next();
				// 商品名称不可为空, 售价不为null且不为0, 总库存数量不可为空也不可为0, 商品上限不可为空且商品上限不能大于库存数量
				if (StringUtils.isNotEmpty(commodity.getName()) && commodity.getPrice() != null && commodity.getPrice().compareTo(BigDecimal.ZERO) != 0 && commodity.getTotalInventory() != null && commodity.getTotalInventory() != 0
						&& commodity.getUpperlimit() != null && commodity.getUpperlimit() <= commodity.getTotalInventory()) {
					if (iterator.hasNext()) {
						continue;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/** 判断企业团是否可以发布 */
	public Boolean isEnterpriseRelease() {
		long thisTime = System.currentTimeMillis();
		// 状态为 待发布, 主题不可为空, 主题介绍不可为空, 拼团须知不可为空, 开始时间不可为空, 结束时间不可为空並且要大于当前时间, 上限单数不可为空也不可为0, 自提地点不可为空, 自提时间不可为空并且要大于结束时间, 且电话号码不可为空, 且小区不可为空, 且当可升级商品为true添加积分不可为null且要大于0
		if (this.getGroupOrderState().getStates() == States.DFB && StringUtils.isNotEmpty(this.getTheme()) && StringUtils.isNotEmpty(this.getThemeIntroduce()) && StringUtils.isNotEmpty(this.getNotice()) && this.getBeginTime() != null
				&& this.getEndTime() != null && this.getEndTime().getTime() > thisTime && this.getIsCombination() != null && StringUtils.isNotEmpty(this.getSelfExtractingAddress()) && this.getSelfExtractingTime() != null
				&& this.getSelfExtractingEndTime() != null && StringUtils.isNotEmpty(this.getPhone()) && this.weChatUser.getCommunity() != null && (this.isUpgrade && this.addIntegral != null && this.addIntegral > 0)) {
			// 验证商品品目
			Set<GroupCommodityBasic> groupCommodityBasics = this.getGroupCommodityBasic();
			if (groupCommodityBasics.isEmpty()) {
				return false;
			}
			int k = 0;
			for (GroupCommodityBasic groupCommodityBasic : groupCommodityBasics) {
				// 当团单isUpgrade为true（可升级），下面所添加的商品必须有一个为升级商品，且这个升级的商品是隐藏的
				if (this.isUpgrade) {
					Set<GroupCommodity> groupCommoditys = groupCommodityBasic.getGroupCommoditys();
					if (groupCommoditys.isEmpty()) {
						return false;
					}
					for (GroupCommodity groupCommodity : groupCommoditys) {
						if (groupCommodity.getIsUpgrade() != null && groupCommodity.getIsUpgrade() && groupCommodity.getIsHidden() != null && groupCommodity.getIsHidden()) {
							++k;
						}
					}
				}
			}
			if (k == 0) {
				return false;
			}
			// 验证商品品目和商品信息
			for (GroupCommodityBasic groupCommodityBasic : groupCommodityBasics) {
				return groupCommodityBasic.isGroupCommodityBasicRelease();
			}
		}
		return false;
	}

	/** 已上传过提货照的量 */
	public Integer getPickPicOrders() {
		int i = 0;
		for (Order order : this.getOrders()) {
			for (Annex annex : order.getAnnexs()) {
				if (annex != null && annex.getAnnexType() == AnnexType.ORDER_PICK_PIC) {
					i++;
					break;
				}
			}
		}
		return i;
	}

	/** 已完成的订单的数量（有效订单的数量, 订单待付款且待支付、待收货且已支付、已完成且已支付的订单） */
	public Integer getValidOrders() {
		int i = 0;
		for (Order order : this.getOrders()) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			PayState payState = order.getPayState();
			if ((orderStates == OrderStates.DFK && payState == PayState.DZF) || (orderStates == OrderStates.DSH && payState == PayState.YZF) || (orderStates == OrderStates.YWC && payState == PayState.YZF)) {
				i++;
			}
		}
		return i;
	}

	/** 获取已付款订单的数量 */
	public Integer getPayOrders() {
		int i = 0;
		for (Order order : this.getOrders()) {
			OrderStates orderStates = order.getOrderState().getOrderStates();
			if ((orderStates == OrderStates.DSH || orderStates == OrderStates.YWC) && order.getPayState() == PayState.YZF) {
				i++;
			}
		}
		return i;
	}

	/** 判断团单下的订单状态是否存在支付中或退款中的订单 */
	public Boolean isOrderPayStateByZfzOrTkz() {
		for (Order order : this.getOrders()) {
			PayState payState = order.getPayState();
			if (payState == PayState.ZFZ || payState == PayState.YZSB || payState == PayState.TKZ || payState == PayState.DTK || payState == PayState.TKSB) {
				return false;
			}
		}
		return true;
	}

	/** 判断当前用户所属小区和团单所属小区是否是同一小区（1 表示在同一个小区，0 表示不在同一个小区） */
	public Integer isSameCommunity(WeChatUser weChatUser) {
		// 用户小区
		Community userCommunity = weChatUser.getCommunity();
		Community groupCommunity = this.getCommunity();
		if (userCommunity == null || groupCommunity == null) {
			return 0;
		} else {
			// 直接比较ID是否相等，不用再判断是否为用一个市区等
			if (userCommunity.getId() == groupCommunity.getId()) {
				return 1;
			}
		}
		return 0;
	}

	/** 判断用户是否存在当前团单下未付款的订单 */
	public Boolean isGroupOrderByWeChatUserOrder(WeChatUser weChatUser) {
		List<Order> list = new OrderQuery().groupOrder(this).weChatUser(weChatUser).orderStateQuery(new OrderStateQuery().orderStates(OrderStates.DFK)).payState(PayState.DZF).list();
		if (!list.isEmpty()) {
			return true;
		}
		return false;
	}
}
