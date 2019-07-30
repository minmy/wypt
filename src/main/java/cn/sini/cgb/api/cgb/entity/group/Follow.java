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
 * 关注实体
 * 
 * @author gaowei
 */
@Entity
@Table(name = Follow.TABLE_NAME)
public class Follow extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_FOLLOW";

	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;

	/** 用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_WECHAT_USER", nullable = false)
	private WeChatUser weChatUser;

	/** 被关注用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_FOLLOW_USER", nullable = false)
	private WeChatUser followUser;

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
	 * 获取用户
	 * 
	 * @return weChatUser用户
	 */
	public WeChatUser getWeChatUser() {
		return weChatUser;
	}

	/**
	 * 设置用户
	 * 
	 * @param weChatUser 用户
	 */
	public void setWeChatUser(WeChatUser weChatUser) {
		this.weChatUser = weChatUser;
	}

	/**
	 * 获取关注用户
	 * 
	 * @return followUser关注用户
	 */
	public WeChatUser getFollowUser() {
		return followUser;
	}

	/**
	 * 设置关注用户
	 * 
	 * @param followUser 关注用户
	 */
	public void setFollowUser(WeChatUser followUser) {
		this.followUser = followUser;
	}

}
