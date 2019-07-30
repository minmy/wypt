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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import cn.sini.cgb.admin.identity.entity.address.ShippingAddress;
import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision;
import cn.sini.cgb.admin.identity.entity.address.TaAdministrativeDivision.DivisionEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 小区(社区)实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = Community.TABLE_NAME)
public class Community extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_COMMUNITY";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 小区名称 */
	@Column(name = "NAME_")
	private String name;

	/** 备注 */
	@Column(name = "REMARKS")
	private String remarks;

	/** 排序 */
	@Column(name = "SORT_")
	private Integer sort;

	/** 用户集合 */
	@OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<WeChatUser> weChatUsers = new HashSet<WeChatUser>();

	/** 所属镇区(一期，二期不再维护) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_TOWNSHIP")
	private Township township;

	/** 所属里长 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHATUSER_LIZHANG")
	private WeChatUser weChatUser;

	/** 团单集合 */
	@OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	private Set<GroupOrder> groupOrders = new HashSet<GroupOrder>();

	/** 新的所属镇/街道 （二期统一使用） */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_DIVISION")
	private TaAdministrativeDivision town;

	/** 拥有的配送地址集合 */
	@OneToMany(mappedBy = "community", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	@OrderBy("createTime asc")
	private Set<ShippingAddress> shippingAddresses = new HashSet<ShippingAddress>();

	/** 所有上级区划名称（瞬时字段） */
	@Transient
	private String allDivisionName;

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
	 * 获取小区名称
	 * 
	 * @return name小区名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置小区名称
	 * 
	 * @param name 小区名称
	 */
	public void setName(String name) {
		this.name = name;
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
	 * 获取所属用户
	 * 
	 * @return weChatUsers所属用户
	 */
	public Set<WeChatUser> getWeChatUsers() {
		return weChatUsers;
	}

	/**
	 * 设置所属用户
	 * 
	 * @param weChatUsers 所属用户
	 */
	public void setWeChatUsers(Set<WeChatUser> weChatUsers) {
		this.weChatUsers = weChatUsers;
	}

	/**
	 * 获取所属镇区
	 * 
	 * @return township所属镇区
	 */
	public Township getTownship() {
		return township;
	}

	/**
	 * 设置所属镇区
	 * 
	 * @param township 所属镇区
	 */
	public void setTownship(Township township) {
		this.township = township;
	}

	/**
	 * 获取所属里长
	 * 
	 * @return weChatUser 所属里长
	 */
	public WeChatUser getWeChatUser() {
		return weChatUser;
	}

	/**
	 * 设置所属里长
	 * 
	 * @param weChatUser 所属里长
	 */
	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	/**
	 * 获取团单集合
	 * 
	 * @return groupOrders团单集合
	 */
	public Set<GroupOrder> getGroupOrders() {
		return groupOrders;
	}

	/**
	 * 设置团单集合
	 * 
	 * @param groupOrders 团单集合
	 */
	public void setGroupOrders(Set<GroupOrder> groupOrders) {
		this.groupOrders = groupOrders;
	}

	/**
	 * 获取新的所属镇街道（二期统一使用）
	 *
	 * @return town新的所属镇街道（二期统一使用）
	 */
	public TaAdministrativeDivision getTown() {
		return town;
	}

	/**
	 * 设置新的所属镇街道（二期统一使用）
	 *
	 * @param town 新的所属镇街道（二期统一使用）
	 */
	public void setTown(TaAdministrativeDivision town) {
		this.town = town;
	}

	/**
	 * 获取拥有的配送地址集合
	 *
	 * @return shippingAddresses拥有的配送地址集合
	 */
	public Set<ShippingAddress> getShippingAddresses() {
		return shippingAddresses;
	}

	/**
	 * 设置拥有的配送地址集合
	 *
	 * @param shippingAddresses 拥有的配送地址集合
	 */
	public void setShippingAddresses(Set<ShippingAddress> shippingAddresses) {
		this.shippingAddresses = shippingAddresses;
	}

	/**
	 * 设置所有上级区划名称（瞬时字段）
	 * 
	 * @param allDivisionName 所有上级区划名称（瞬时字段）
	 */
	public void setAllDivisionName(String allDivisionName) {
		this.allDivisionName = allDivisionName;
	}

	/**
	 * 获取所有的上级区划名称
	 *
	 * @return
	 */
	public String getAllDivisionName() {
		String s = null;
		if (town != null) {
			DivisionEnum divisionType = town.getDivisionType();
			if (divisionType.name().equals("city")) {
				s = town.getParentDivision().getFullName() + "-" + town.getFullName();
			} else if (divisionType.name().equals("area_or_county")) {
				s = town.getParentDivision().getParentDivision().getFullName() + "-" + town.getParentDivision().getFullName() + "-" + town.getFullName();
			} else if (divisionType.name().equals("town_or_street")) {
				s = town.getParentDivision().getParentDivision().getParentDivision().getFullName() + "-" + town.getParentDivision().getParentDivision().getFullName() + "-" + town.getParentDivision().getFullName() + "-" + town.getFullName();
			}
		}
		allDivisionName = s;
		return allDivisionName;
	}
}