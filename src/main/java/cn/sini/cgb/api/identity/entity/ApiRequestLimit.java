package cn.sini.cgb.api.identity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.sini.cgb.admin.identity.entity.User;
import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 接口请求限制实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiRequestLimit.TABLE_NAME)
public class ApiRequestLimit extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_REQUEST_LIMIT";

	/** ID */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	/** 请求标识 */
	@Column(name = "REQUEST_ID", nullable = false)
	private String requestId;

	/** 所属用户 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_USER", nullable = false)
	private User user;

	/**
	 * 获取ID
	 * 
	 * @return id ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取请求标识
	 * 
	 * @return requestId 请求标识
	 */
	public String getRequestId() {
		return requestId;
	}

	/**
	 * 设置请求标识
	 * 
	 * @param requestId 请求标识
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * 获取所属用户
	 * 
	 * @return user 所属用户
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 设置所属用户
	 * 
	 * @param user 所属用户
	 */
	public void setUser(User user) {
		this.user = user;
	}
}