package cn.sini.cgb.api.cgb.entity.group;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Where;

import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.ApplyWithdrawMoney;
import cn.sini.cgb.api.identity.entity.ApiRole;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 微信用户实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = WeChatUser.TABLE_NAME)
public class WeChatUser extends AbstractLogicalRemoveEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_WECHAT_USER";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** OpenId */
	@Column(name = "OPEN_ID", unique = true, nullable = false)
	private String openId;

	/** unionId */
	@Column(name = "UNION_ID", nullable = false)
	private String unionId;

	/** SessionKey */
	@Column(name = "SESSION_KEY")
	private String sessionKey;

	/** 微信昵称 */
	@Column(name = "NAME")
	private String name;

	/** 微信头像地址 */
	@Column(name = "HEAD_IMG_URL")
	private String headImgUrl;

	/** 联系电话 */
	@Column(name = "PHONE")
	private String phone;

	/** 联系人（昵称） */
	@Column(name = "CONTACTS")
	private String contacts;

	/** 申请提现联系电话 */
	@Column(name = "APPLY_PHONE")
	private String applyPhone;

	/** 申请人真实姓名 */
	@Column(name = "APPLY_REAL_NAME")
	private String applyRealName;

	/** 申请人微信号 */
	@Column(name = "APPLY_WECHAT")
	private String applyWeChat;

	/** 省 */
	@Column(name = "PROVINCE")
	private String province;

	/** 市 */
	@Column(name = "CITY")
	private String city;

	/** 区 */
	@Column(name = "AREA")
	private String area;

	/** 镇 */
	@Column(name = "TOWN")
	private String town;

	/** 所属小区 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_COMMUNITY")
	private Community community;

	/** 详细地址 */
	@Column(name = "ADDRESS")
	private String address;

	/** 是否里长 */
	@Column(name = "BRIGADIER", nullable = false)
	private Boolean brigadier = false;

	/** 是否企业 */
	@Column(name = "IS_BUSINESS", nullable = false)
	private Boolean isBusiness = false;

	/** 所属团单 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<GroupOrder> groupOrder = new HashSet<GroupOrder>();

	/** 所属订单 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Order> orders = new HashSet<Order>();

	/** 记账流水 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<AllBill> allBills = new HashSet<AllBill>();

	/** 分享集合 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Share> shares = new HashSet<Share>();

	/** 用户操作分享集合 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<ShareWeChatUser> shareWeChatUsers = new HashSet<ShareWeChatUser>();

	/** 关注集合 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Follow> follows = new HashSet<Follow>();

	/** 被关注集合 */
	@OneToMany(mappedBy = "followUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Follow> followUsers = new HashSet<Follow>();

	/** 提现申请记录集合 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<ApplyWithdrawMoney> applyWithdrawMoneys = new HashSet<ApplyWithdrawMoney>();

	/** 拥有的微信角色集合 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "T_WECHAT_ROLE_USER", joinColumns = @JoinColumn(name = "FK_WECHAT_USER"), inverseJoinColumns = @JoinColumn(name = "FK_API_ROLE"))
	private Set<ApiRole> apiRoles = new HashSet<ApiRole>();

	/** 里长可管理小区集合 */
	@OneToMany(mappedBy = "weChatUser", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<Community> communities = new HashSet<Community>();

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
	 * 获取OpenId
	 * 
	 * @return openIdOpenId
	 */
	public String getOpenId() {
		return openId;
	}

	/**
	 * 设置OpenId
	 * 
	 * @param openId OpenId
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}

	/**
	 * 获取unionId
	 * 
	 * @return unionIdunionId
	 */
	public String getUnionId() {
		return unionId;
	}

	/**
	 * 设置unionId
	 * 
	 * @param unionId unionId
	 */
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	/**
	 * 获取SessionKey
	 * 
	 * @return sessionKeySessionKey
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * 设置SessionKey
	 * 
	 * @param sessionKey SessionKey
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
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
	 * 获取联系人（昵称）
	 * 
	 * @return contacts联系人（昵称）
	 */
	public String getContacts() {
		return contacts;
	}

	/**
	 * 设置联系人（昵称）
	 * 
	 * @param contacts 联系人（昵称）
	 */
	public void setContacts(String contacts) {
		this.contacts = contacts;
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
	 * 获取区
	 * 
	 * @return area区
	 */
	public String getArea() {
		return area;
	}

	/**
	 * 设置区
	 * 
	 * @param area 区
	 */
	public void setArea(String area) {
		this.area = area;
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
	 * 获取是否里长
	 * 
	 * @return brigadier 是否里长
	 */
	public Boolean getBrigadier() {
		return brigadier;
	}

	/**
	 * 设置是否里长
	 * 
	 * @param brigadier 是否里长
	 */
	public void setBrigadier(Boolean brigadier) {
		this.brigadier = brigadier;
	}

	/**
	 * 获取所属团单
	 * 
	 * @return groupOrder所属团单
	 */
	public Set<GroupOrder> getGroupOrder() {
		return groupOrder;
	}

	/**
	 * 设置所属团单
	 * 
	 * @param groupOrder 所属团单
	 */
	public void setGroupOrder(Set<GroupOrder> groupOrder) {
		this.groupOrder = groupOrder;
	}

	public Set<AllBill> getAllBills() {
		return allBills;
	}

	public void setAllBills(Set<AllBill> allBills) {
		this.allBills = allBills;
	}

	/**
	 * 获取所属订单
	 * 
	 * @return orders所属订单
	 */
	public Set<Order> getOrders() {
		return orders;
	}

	/**
	 * 设置所属订单
	 * 
	 * @param orders 所属订单
	 */
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取微信昵称
	 * 
	 * @return name微信昵称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置微信昵称
	 * 
	 * @param name 微信昵称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取微信头像地址
	 * 
	 * @return headImgUrl微信头像地址
	 */
	public String getHeadImgUrl() {
		return headImgUrl;
	}

	/**
	 * 设置微信头像地址
	 * 
	 * @param headImgUrl 微信头像地址
	 */
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	/**
	 * 获取分享
	 * 
	 * @return shares分享
	 */
	public Set<Share> getShares() {
		return shares;
	}

	/**
	 * 设置分享
	 * 
	 * @param shares 分享
	 */
	public void setShares(Set<Share> shares) {
		this.shares = shares;
	}

	/**
	 * 获取关注集合
	 * 
	 * @return follows关注集合
	 */
	public Set<Follow> getFollows() {
		return follows;
	}

	/**
	 * 设置关注集合
	 * 
	 * @param follows 关注集合
	 */
	public void setFollows(Set<Follow> follows) {
		this.follows = follows;
	}

	/**
	 * 获取被关注集合
	 * 
	 * @return followUsers被关注集合
	 */
	public Set<Follow> getFollowUsers() {
		return followUsers;
	}

	/**
	 * 设置被关注集合
	 * 
	 * @param followUsers 被关注集合
	 */
	public void setFollowUsers(Set<Follow> followUsers) {
		this.followUsers = followUsers;
	}

	/**
	 * 获取申请提现联系电话
	 * 
	 * @return applyPhone申请提现联系电话
	 */
	public String getApplyPhone() {
		return applyPhone;
	}

	/**
	 * 设置申请提现联系电话
	 * 
	 * @param applyPhone 申请提现联系电话
	 */
	public void setApplyPhone(String applyPhone) {
		this.applyPhone = applyPhone;
	}

	/**
	 * 获取申请人真实姓名
	 * 
	 * @return applyRealName申请人真实姓名
	 */
	public String getApplyRealName() {
		return applyRealName;
	}

	/**
	 * 设置申请人真实姓名
	 * 
	 * @param applyRealName 申请人真实姓名
	 */
	public void setApplyRealName(String applyRealName) {
		this.applyRealName = applyRealName;
	}

	/**
	 * 获取申请人微信号
	 * 
	 * @return applyWeChat申请人微信号
	 */
	public String getApplyWeChat() {
		return applyWeChat;
	}

	/**
	 * 设置申请人微信号
	 * 
	 * @param applyWeChat 申请人微信号
	 */
	public void setApplyWeChat(String applyWeChat) {
		this.applyWeChat = applyWeChat;
	}

	/**
	 * 获取提现申请记录集合
	 * 
	 * @return applyWithdrawMoneys提现申请记录集合
	 */
	public Set<ApplyWithdrawMoney> getApplyWithdrawMoneys() {
		return applyWithdrawMoneys;
	}

	/**
	 * 设置提现申请记录集合
	 * 
	 * @param applyWithdrawMoneys 提现申请记录集合
	 */
	public void setApplyWithdrawMoneys(Set<ApplyWithdrawMoney> applyWithdrawMoneys) {
		this.applyWithdrawMoneys = applyWithdrawMoneys;
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
	 * 获取拥有的微信角色集合
	 * 
	 * @return apiRoles 拥有的微信角色集合
	 */
	public Set<ApiRole> getApiRoles() {
		return apiRoles;
	}

	/**
	 * 设置拥有的微信角色集合
	 * 
	 * @param apiRoles 拥有的微信角色集合
	 */
	public void setApiRoles(Set<ApiRole> apiRoles) {
		this.apiRoles = apiRoles;
	}

	/**
	 * 获取里长可管理小区集合
	 * 
	 * @return communities 里长可管理小区集合
	 */
	public Set<Community> getCommunities() {
		return communities;
	}

	/**
	 * 设置里长可管理小区集合
	 * 
	 * @param communities 里长可管理小区集合
	 */
	public void setCommunities(Set<Community> communities) {
		this.communities = communities;
	}

	/**
	 * 获取是否企业
	 * 
	 * @return isBusiness是否企业
	 */
	public Boolean getIsBusiness() {
		return isBusiness;
	}

	/**
	 * 设置是否企业
	 * 
	 * @param isBusiness 是否企业
	 */
	public void setIsBusiness(Boolean isBusiness) {
		this.isBusiness = isBusiness;
	}

	/**
	 * 获取用户操作分享集合
	 * 
	 * @return shareWeChatUsers用户操作分享集合
	 */
	public Set<ShareWeChatUser> getShareWeChatUsers() {
		return shareWeChatUsers;
	}

	/**
	 * 设置用户操作分享集合
	 * 
	 * @param shareWeChatUsers 用户操作分享集合
	 */
	public void setShareWeChatUsers(Set<ShareWeChatUser> shareWeChatUsers) {
		this.shareWeChatUsers = shareWeChatUsers;
	}

	/**
	 * 获取镇
	 * 
	 * @return town镇
	 */
	public String getTown() {
		return town;
	}

	/**
	 * 设置镇
	 * 
	 * @param town 镇
	 */
	public void setTown(String town) {
		this.town = town;
	}
}
