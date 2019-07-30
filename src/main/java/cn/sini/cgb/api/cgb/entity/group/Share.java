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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Where;

import cn.sini.cgb.admin.annex.entity.Annex;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;

/**
 * 分享实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = Share.TABLE_NAME)
public class Share extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_SHARE";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 用户集合 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 团单集合 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER", nullable = false)
	private GroupOrder groupOrder;

	/** 分享合成图 */
	@OneToOne(mappedBy = "share", fetch = FetchType.LAZY)
	private Annex annex;

	/** 分享随机数 */
	@Column(name = "RANDOM_NUMBER", unique = true)
	private String randomNumber;

	/** 用户操作分享集合 */
	@OneToMany(mappedBy = "share", fetch = FetchType.LAZY)
	@Where(clause = "remove='false'")
	@OrderBy("updateTime desc")
	private Set<ShareWeChatUser> shareWeChatUsers = new HashSet<ShareWeChatUser>();

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
	 * 获取用户集合
	 * 
	 * @return weChatUser用户集合
	 */
	public WeChatUser getWeChatUser() {
		return weChatUser;
	}

	/**
	 * 设置用户集合
	 * 
	 * @param weChatUser 用户集合
	 */
	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	/**
	 * 获取团单集合
	 * 
	 * @return groupOrder团单集合
	 */
	public GroupOrder getGroupOrder() {
		return groupOrder;
	}

	/**
	 * 设置团单集合
	 * 
	 * @param groupOrder 团单集合
	 */
	public void setGroupOrder(GroupOrder groupOrder) {
		this.groupOrder = groupOrder;
	}

	/**
	 * 获取分享合成图
	 * 
	 * @return annex分享合成图
	 */
	public Annex getAnnex() {
		return annex;
	}

	/**
	 * 设置分享合成图
	 * 
	 * @param annex 分享合成图
	 */
	public void setAnnex(Annex annex) {
		this.annex = annex;
	}

	/**
	 * 获取分享随机数
	 * 
	 * @return randomNumber分享随机数
	 */
	public String getRandomNumber() {
		return randomNumber;
	}

	/**
	 * 设置分享随机数
	 * 
	 * @param randomNumber 分享随机数
	 */
	public void setRandomNumber(String randomNumber) {
		this.randomNumber = randomNumber;
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
}
