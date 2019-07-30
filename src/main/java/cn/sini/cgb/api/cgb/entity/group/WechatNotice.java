package cn.sini.cgb.api.cgb.entity.group;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 微信模板消息通知实体
 * 
 * @author 
 */
@Entity
@Table(name = WechatNotice.TABLE_NAME)
public class WechatNotice extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_WECHAT_NOTICE";

	/** 通知状态枚举 */
	public enum NoticeStatus {
		/** 发送成功 */
		SUCCESS("发送"),
		/** 发送失败 */
		ERROR("发送失败"),
		/** 发送异常 */
		EXCEPTION("发送异常");

		private String desc;

		private NoticeStatus(String desc) {
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

	/** 通知类型枚举 */
	public enum NoticeType {
		/** 发货通知 */
		DELIVERY("发货通知"),
		/** 取消拼团通知 */
		CANCEL_GROUP("取消拼团通知"),
		/** 自提时间修改通知 */
		SELF_EXTRACTING_TIME("自提时间修改通知"),
		/** 参团成功提醒 */
		JOINGROUP("参团成功提醒");

		private String desc;

		private NoticeType(String desc) {
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
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	/** 接收用户的OpenId */
	@Column(name = "TOUSER", nullable = false)
	private String touser;

	/** 使用的模板 */
	@Column(name = "TEMPLATE_ID", nullable = false)
	private String templateId;

	/** 点击消息打开的链接 */
	@Column(name = "URL", length = 2000)
	private String url;

	/** 模板消息的内容 */
	@Column(name = "DATA", nullable = false, length = 2000)
	private String data;

	/** 通知状态 */
	@Enumerated(EnumType.STRING)
	@Column(name = "NOTICE_STATUS", nullable = false)
	private NoticeStatus noticeStatus;

	/** 通知类别 */
	@Enumerated(EnumType.STRING)
	@Column(name = "NOTICE_TYPE", nullable = false)
	private NoticeType noticeType;

	/** 微信接口的返回值 */
	@Column(name = "RETURN_MESSAGE")
	private String returnMessage;

	/** 通知实际发送时间 */
	@Column(name = "SEND_TIME")
	private Date sendTime;
	
	/** 所属团单ID */
	@Column(name = "GROUP_ID")
	private Long groupId;

	/** 所属团单ID */
	public Long getGroupId() {
		return groupId;
	}
	/** 所属团单ID */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	/** OpenId */
	@Column(name = "OPEN_ID")
	private String openId;
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/**
	 * 获取通知类别
	 * 
	 * @return noticeType 通知类别
	 */
	public NoticeType getNoticeType() {
		return noticeType;
	}

	/**
	 * 设置通知类别
	 * 
	 * @param noticeType 通知类别
	 */
	public void setNoticeType(NoticeType noticeType) {
		this.noticeType = noticeType;
	}

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
	 * 获取接收用户的OpenId
	 * 
	 * @return touser 接收用户的OpenId
	 */
	public String getTouser() {
		return touser;
	}

	/**
	 * 设置接收用户的OpenId
	 * 
	 * @param touser 接收用户的OpenId
	 */
	public void setTouser(String touser) {
		this.touser = touser;
	}

	/**
	 * 获取使用的模板
	 * 
	 * @return templateId 使用的模板
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * 设置使用的模板
	 * 
	 * @param templateId 使用的模板
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * 获取点击消息打开的链接
	 * 
	 * @return url 点击消息打开的链接
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置点击消息打开的链接
	 * 
	 * @param url 点击消息打开的链接
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取模板消息的内容
	 * 
	 * @return data 模板消息的内容
	 */
	public String getData() {
		return data;
	}

	/**
	 * 设置模板消息的内容
	 * 
	 * @param data 模板消息的内容
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * 获取通知状态
	 * 
	 * @return noticeStatus 通知状态
	 */
	public NoticeStatus getNoticeStatus() {
		return noticeStatus;
	}

	/**
	 * 设置通知状态
	 * 
	 * @param noticeStatus 通知状态
	 */
	public void setNoticeStatus(NoticeStatus noticeStatus) {
		this.noticeStatus = noticeStatus;
	}

	/**
	 * 获取微信接口的返回值
	 * 
	 * @return returnMessage 微信接口的返回值
	 */
	public String getReturnMessage() {
		return returnMessage;
	}

	/**
	 * 设置微信接口的返回值
	 * 
	 * @param returnMessage 微信接口的返回值
	 */
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	/**
	 * 获取通知实际发送时间
	 * 
	 * @return sendTime 通知实际发送时间
	 */
	public Date getSendTime() {
		return sendTime;
	}

	/**
	 * 设置通知实际发送时间
	 * 
	 * @param sendTime 通知实际发送时间
	 */
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
}