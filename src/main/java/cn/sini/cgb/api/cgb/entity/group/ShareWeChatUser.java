package cn.sini.cgb.api.cgb.entity.group;

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
 * 分享记录表和用户表的中间表（用于记录有哪些用户打开了分享记录）
 * 
 * @author gaowei
 */
@Entity
@Table(name = ShareWeChatUser.TABLE_NAME)
public class ShareWeChatUser extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_SHARE_WECHATUSER";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 所属分享记录 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_SHARE", nullable = false)
	private Share share;

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
	 * 获取所属分享记录
	 * 
	 * @return share所属分享记录
	 */
	public Share getShare() {
		return share;
	}

	/**
	 * 设置所属分享记录
	 * 
	 * @param share 所属分享记录
	 */
	public void setShare(Share share) {
		this.share = share;
	}
}
