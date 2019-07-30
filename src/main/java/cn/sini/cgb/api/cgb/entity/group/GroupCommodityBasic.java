package cn.sini.cgb.api.cgb.entity.group;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import cn.sini.cgb.api.cgb.entity.group.WayOfDelivery.DeliveryType;
import cn.sini.cgb.api.cgb.query.group.WayOfDeliveryQuery;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 商品品目信息（目前在开‘企业团’时用到）
 * 
 * @author gaowei
 */
@Entity
@Table(name = GroupCommodityBasic.TABLE_NAME)
public class GroupCommodityBasic extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_GROUP_COMMODITY_BASIC";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 商品名称 */
	@Column(name = "NAME")
	private String name;

	/** 商品是否打包(若为true时，该商品品目下的所有商品的价格失效，商品价格以商品品目价格为准。若为false时，则使用各个商品自身的价格) */
	@Column(name = "IS_PACK")
	private Boolean isPack;

	/** 售价(当isPack为true时使用) */
	@Column(name = "PRICE")
	private BigDecimal price;

	/** 说明 */
	@Column(name = "DESCRIPTION")
	private String description;

	/** 排序 */
	@Column(name = "SORT")
	private Integer sort;

	/** 所属团单 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER", nullable = false)
	private GroupOrder groupOrder;

	/** 商品集合 */
	@OneToMany(mappedBy = "groupCommodityBasic", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	@OrderBy("sort asc")
	private Set<GroupCommodity> groupCommoditys = new HashSet<GroupCommodity>();

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
	 * 获取说明
	 * 
	 * @return description说明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置说明
	 * 
	 * @param description 说明
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * 获取商品集合
	 * 
	 * @return groupCommoditys商品集合
	 */
	public Set<GroupCommodity> getGroupCommoditys() {
		return groupCommoditys;
	}

	/**
	 * 设置商品集合
	 * 
	 * @param groupCommoditys 商品集合
	 */
	public void setGroupCommoditys(Set<GroupCommodity> groupCommoditys) {
		this.groupCommoditys = groupCommoditys;
	}

	/**
	 * 获取商品是否打包(若为true时，该商品品目下的所有商品的价格失效，商品价格以商品品目价格为准。若为false时，则使用各个商品自身的价格)
	 * 
	 * @return isPack商品是否打包(若为true时，该商品品目下的所有商品的价格失效，商品价格以商品品目价格为准。若为false时，则使用各个商品自身的价格)
	 */
	public Boolean getIsPack() {
		return isPack;
	}

	/**
	 * 设置商品是否打包(若为true时，该商品品目下的所有商品的价格失效，商品价格以商品品目价格为准。若为false时，则使用各个商品自身的价格)
	 * 
	 * @param isPack 商品是否打包(若为true时，该商品品目下的所有商品的价格失效，商品价格以商品品目价格为准。若为false时，则使用各个商品自身的价格)
	 */
	public void setIsPack(Boolean isPack) {
		this.isPack = isPack;
	}

	/**
	 * 获取售价(当isCombination为true时使用)
	 * 
	 * @return price售价(当isCombination为true时使用)
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * 设置售价(当isCombination为true时使用)
	 * 
	 * @param price 售价(当isCombination为true时使用)
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
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

	/** 企业团发布验证商品品目信息 */
	public Boolean isGroupCommodityBasicRelease() {
		Set<WayOfDelivery> wayOfDeliverys = this.groupOrder.getWayOfDeliverys();
		WayOfDelivery wayOfDelivery = new WayOfDeliveryQuery().deliveryType(DeliveryType.HX).uniqueResult();
		if (StringUtils.isNotBlank(this.name)) {
			if (this.groupCommoditys.isEmpty()) {
				return false;
			}
			Iterator<GroupCommodity> commodityIterator = this.groupCommoditys.iterator();
			// 如果打包为true，则价格不能为空。那只能控制只有一个商品可以显示出来。
			if (this.isPack) {
				if (this.price != null && this.price.compareTo(BigDecimal.ZERO) != 0) {
					int i = 0;
					while (commodityIterator.hasNext()) {
						GroupCommodity commodity = commodityIterator.next();
						// 如果团单支持核销方式，则核销次数不为空并大于0
						if (wayOfDeliverys.contains(wayOfDelivery)) {
							if (commodity.getWriteOffsNumber() == null || commodity.getWriteOffsNumber() <= 0) {
								return false;
							}
						}
						if (!commodity.getIsHidden()) {
							++i;
						}
						// 商品名称不可为空, 售价不为null且不为0, 总库存数量不可为空也不可为0, 商品上限不可为空且商品上限不能大于库存数量, 是否是升级商品不可为空, 且当企业收货方式为核销时核销次数不为空并大于0
						if (StringUtils.isNotEmpty(commodity.getName()) && commodity.getTotalInventory() != null && commodity.getTotalInventory() != 0 && commodity.getUpperlimit() != null
								&& commodity.getUpperlimit() <= commodity.getTotalInventory() && commodity.getIsUpgrade() != null && commodity.getVerify() != null) {
							if (commodityIterator.hasNext()) {
								continue;
							} else {
								if (i != 1) {
									return false;
								}
								return true;
							}
						} else {
							return false;
						}
					}
				}
			} else {
				int i = 0;
				while (commodityIterator.hasNext()) {
					GroupCommodity commodity = commodityIterator.next();
					// 如果团单支持核销方式，则核销次数不为空并大于0
					if (wayOfDeliverys.contains(wayOfDelivery)) {
						if (commodity.getWriteOffsNumber() == null || commodity.getWriteOffsNumber() <= 0) {
							return false;
						}
					}
					// 如果品目没有打包，则控制商品不能全部隐藏，至少有一个显示(因为可能会有可以升级的商品，所以这种情况下允许商品隐藏)
					if (!commodity.getIsHidden()) {
						++i;
					}
					// 商品名称不可为空, 售价不为null且不为0, 总库存数量不可为空也不可为0, 商品上限不可为空且商品上限不能大于库存数量, 是否是升级商品不可为空, 且核销次数不为空并大于0
					if (StringUtils.isNotEmpty(commodity.getName()) && commodity.getPrice() != null && commodity.getPrice().compareTo(BigDecimal.ZERO) != 0 && commodity.getTotalInventory() != null && commodity.getTotalInventory() != 0
							&& commodity.getUpperlimit() != null && commodity.getUpperlimit() <= commodity.getTotalInventory() && commodity.getIsUpgrade() != null && commodity.getVerify() != null) {
						if (commodityIterator.hasNext()) {
							continue;
						} else {
							if (i == 0) {
								return false;
							}
							return true;
						}
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}
}
