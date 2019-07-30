package cn.sini.cgb.api.identity.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.sini.cgb.common.entity.AbstractEntity;

/**
 * 接口访问日志实体
 * 
 * @author 杨海彬
 */
@Entity
@Table(name = ApiAccessLog.TABLE_NAME)
public class ApiAccessLog extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_API_ACCESS_LOG";

	/** ID */
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	/** 帐号 */
	@Column(name = "USERNAME", nullable = false)
	private String username;

	/** IP */
	@Column(name = "IP", nullable = false)
	private String ip;

	/** 请求地址 */
	@Column(name = "URL", nullable = false)
	private String url;

	/** 请求头信息 */
	@Lob
	@Column(name = "HEADER", nullable = false)
	private String header;

	/** 请求参数 */
	@Lob
	@Column(name = "PARAMETER", nullable = false)
	private String parameter;

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
	 * 获取帐号
	 * 
	 * @return username 帐号
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置帐号
	 * 
	 * @param username 帐号
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取IP
	 * 
	 * @return ip IP
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 设置IP
	 * 
	 * @param ip IP
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * 获取请求地址
	 * 
	 * @return url 请求地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置请求地址
	 * 
	 * @param url 请求地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取请求头信息
	 * 
	 * @return header 请求头信息
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * 设置请求头信息
	 * 
	 * @param header 请求头信息
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * 获取请求参数
	 * 
	 * @return parameter 请求参数
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * 设置请求参数
	 * 
	 * @param parameter 请求参数
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}