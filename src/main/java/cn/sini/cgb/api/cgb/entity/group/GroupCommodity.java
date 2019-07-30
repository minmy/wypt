package cn.sini.cgb.api.cgb.entity.group;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Where;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.query.group.OrderQuery;
import cn.sini.cgb.api.cgb.query.group.OrderStateQuery;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 团单商品实体
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = GroupCommodity.TABLE_NAME)
public class GroupCommodity extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_GROUP_COMMODITY";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 商品名称 */
	@Column(name = "NAME")
	private String name;

	/** 原价 */
	@Column(name = "ORIGINAL_PRICE", precision = 8, scale = 2)
	private BigDecimal originalPrice;

	/** 售价（现价） */
	@Column(name = "PRICE", precision = 8, scale = 2)
	private BigDecimal price;

	/** 规格说明 */
	@Column(name = "DESCRIPTION")
	private String description;

	/** 总库存量 */
	@Column(name = "TOTAL_INVENTORY")
	private Integer totalInventory;

	/** 剩余库存量 */
	@Column(name = "REMNANT_INVENTORY")
	private Integer remnantInventory;

	/** 商品详情 */
	@Column(name = "DETAILS")
	private String details;

	/** 排序 */
	@Column(name = "SORT_")
	private Integer sort;

	/** 商品图片 */
	@OneToMany(mappedBy = "groupCommodity", fetch = FetchType.LAZY)
	@OrderBy("sort asc")
	private Set<Annex> annexs = new HashSet<Annex>();

	/** 所属团单 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER")
	private GroupOrder groupOrder;

	/** 订单商品集合 */
	@OneToMany(mappedBy = "groupCommodity", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<OrderGoods> orderGoods = new HashSet<OrderGoods>();

	/** 上限单数 ，每个用户最多购买的数量，若为0则表示当前商品无上限，否则有上限 */
	@Column(name = "UPPER_LIMIT")
	private Integer upperlimit;

	/** 所属基本商品 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_COMMODITY_BASIC")
	private GroupCommodityBasic groupCommodityBasic;

	/** 是否可升级商品 */
	@Column(name = "IS_UPGRADE")
	private Boolean isUpgrade = false;

	/** 核销次数 */
	@Column(name = "WRITE_OFFS_NUMBER")
	private Integer writeOffsNumber;

	/** 所属商家 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_BUSINESS")
	public Business business;

	/** 商品是否隐藏 */
	@Column(name = "IS_HIDDEN")
	private Boolean isHidden = false;

	/** 所需积分 */
	@Column(name = "INTEGRAL")
	private Integer integral = 0;

	/** 核销(true 代表不可核销，false 代表可核销 默认为可核销) */
	@Column(name = "VERIFY")
	private Boolean verify = false;

	/** 已核销次数（瞬时字段） */
	@Transient
	private Integer verifyNumber;

	/**
	 * 获取核销(true代表不可核销，false代表可核销默认为可核销)
	 * 
	 * @return verify核销(true代表不可核销，false代表可核销默认为可核销)
	 */
	public Boolean getVerify() {
		return verify;
	}

	/**
	 * 设置核销(true代表不可核销，false代表可核销默认为可核销)
	 * 
	 * @param verify 核销(true代表不可核销，false代表可核销默认为可核销)
	 */
	public void setVerify(Boolean verify) {
		this.verify = verify;
	}

	/**
	 * 获取商品是否隐藏
	 * 
	 * @return isHidden商品是否隐藏
	 */
	public Boolean getIsHidden() {
		return isHidden;
	}

	/**
	 * 设置商品是否隐藏
	 * 
	 * @param isHidden 商品是否隐藏
	 */
	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

	/**
	 * 获取核销次数
	 * 
	 * @return writeOffsNumber核销次数
	 */
	public Integer getWriteOffsNumber() {
		return writeOffsNumber;
	}

	/**
	 * 设置核销次数
	 * 
	 * @param writeOffsNumber 核销次数
	 */
	public void setWriteOffsNumber(Integer writeOffsNumber) {
		this.writeOffsNumber = writeOffsNumber;
	}

	/**
	 * 获取所属基本商品
	 * 
	 * @return groupCommodityBasic所属基本商品
	 */
	public GroupCommodityBasic getGroupCommodityBasic() {
		return groupCommodityBasic;
	}

	/**
	 * 设置所属基本商品
	 * 
	 * @param groupCommodityBasic 所属基本商品
	 */
	public void setGroupCommodityBasic(GroupCommodityBasic groupCommodityBasic) {
		this.groupCommodityBasic = groupCommodityBasic;
	}

	/** 上限单数 ，每个用户最多购买的数量 */
	public Integer getUpperlimit() {
		return upperlimit;
	}

	/** 上限单数 ，每个用户最多购买的数量 */
	public void setUpperlimit(Integer upperlimit) {
		this.upperlimit = upperlimit;
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
	 * 获取商品名称
	 * 
	 * @return name商品名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置商品名称
	 * 
	 * @param name 商品名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取售价
	 * 
	 * @return price售价
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置售价
	 * 
	 * @param price 售价
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * 获取规格说明
	 * 
	 * @return description规格说明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置规格说明
	 * 
	 * @param description 规格说明
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取总库存量
	 * 
	 * @return totalInventory总库存量
	 */
	public Integer getTotalInventory() {
		return totalInventory;
	}

	/**
	 * 设置总库存量
	 * 
	 * @param totalInventory 总库存量
	 */
	public void setTotalInventory(Integer totalInventory) {
		this.totalInventory = totalInventory;
	}

	/**
	 * 获取剩余库存量
	 * 
	 * @return remnantInventory剩余库存量
	 */
	public Integer getRemnantInventory() {
		return remnantInventory;
	}

	/**
	 * 设置剩余库存量
	 * 
	 * @param remnantInventory 剩余库存量
	 */
	public void setRemnantInventory(Integer remnantInventory) {
		this.remnantInventory = remnantInventory;
	}

	/**
	 * 获取商品详情
	 * 
	 * @return details商品详情
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * 设置商品详情
	 * 
	 * @param details 商品详情
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * 获取商品图片
	 * 
	 * @return annexs商品图片
	 */
	public Set<Annex> getAnnexs() {
		return annexs;
	}

	/**
	 * 设置商品图片
	 * 
	 * @param annexs 商品图片
	 */
	public void setAnnexs(Set<Annex> annexs) {
		this.annexs = annexs;
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
	 * 获取订单商品集合
	 * 
	 * @return orderGoods订单商品集合
	 */
	public Set<OrderGoods> getOrderGoods() {
		return orderGoods;
	}

	/**
	 * 设置订单商品集合
	 * 
	 * @param orderGoods 订单商品集合
	 */
	public void setOrderGoods(Set<OrderGoods> orderGoods) {
		this.orderGoods = orderGoods;
	}

	/**
	 * 获取排序
	 * 
	 * @return sort排序
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * 设置排序
	 * 
	 * @param sort 排序
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * 获取是否是升级商品
	 * 
	 * @return isUpgrade是否是升级商品
	 */
	public Boolean getIsUpgrade() {
		return isUpgrade;
	}

	/**
	 * 设置是否是升级商品
	 * 
	 * @param isUpgrade 是否是升级商品
	 */
	public void setIsUpgrade(Boolean isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	/**
	 * 获取原价
	 * 
	 * @return originalPrice原价
	 */
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	/**
	 * 设置原价
	 * 
	 * @param originalPrice 原价
	 */
	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
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

	/**
	 * 获取所需积分
	 * 
	 * @return integral所需积分
	 */
	public Integer getIntegral() {
		return integral;
	}

	/**
	 * 设置所需积分
	 * 
	 * @param integral 所需积分
	 */
	public void setIntegral(Integer integral) {
		this.integral = integral;
	}

	public Integer getVerifyNumber() {
		return verifyNumber;
	}

	public void setVerifyNumber(Integer verifyNumber) {
		this.verifyNumber = verifyNumber;
	}

	/** 计算用户剩余商品购买上限数量 */
	public Integer getSurplusUpperlimitNumber(WeChatUser weChatUser) {
		// 如果不等于0，说明该商品有购买上限限制，否则，没有购买上限限制
		Integer upperlimit = this.upperlimit;
		if (upperlimit != 0) {
			// 查询当前用户购买当前团单的所有订单, 过滤已取消的订单
			List<Order> orderList = new OrderQuery().weChatUser(weChatUser).groupOrder(this.groupOrder).orderStateQuery(new OrderStateQuery().orderStatesNe(OrderStates.YQX)).list();
			Integer purchaseAmount = 0;// 购买量
			for (Order order : orderList) {
				for (OrderGoods orderGoods : order.getOrderGoods()) {
					GroupCommodity commodity = orderGoods.getGroupCommodity();
					// 如果存在订单已购买了当前下单的相同的商品，则计算总的该商品购买的数量
					if (commodity.getId().equals(this.id)) {
						purchaseAmount += orderGoods.getAmount();
					}
				}
			}
			return upperlimit - purchaseAmount;
		}
		// 表示没有购买上限限制
		return null;
	}
}
