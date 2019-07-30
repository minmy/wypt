package cn.sini.cgb.common.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sini.cgb.common.util.Environment;

/**
 * 抽象实体类
 * 
 * @author 杨海彬
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);
	public static final String SEQUENCE_TABLE = "T_SEQUENCE";
	public static final String DISCRIMINATOR_COLUMN = "TYPE";

	/** 创建时间 */
	@Column(name = "CREATE_TIME", nullable = false)
	private Date createTime = new Date();

	/** 更新时间 */
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	/**
	 * 获取创建时间
	 * 
	 * @return createTime 创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置创建时间
	 * 
	 * @param createTime 创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取更新时间
	 * 
	 * @return updateTime 更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 设置更新时间
	 * 
	 * @param updateTime 更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/** 获取主键ID值 */
	public abstract Serializable getId();

	/** 新增或更新实例，默认使用当前线程的Hibernate Session */
	public final void saveOrUpdate() {
		saveOrUpdate(Environment.getSession());
	}

	/** 新增或更新实例，手动指定使用的Hibernate Session */
	public void saveOrUpdate(Session session) {
		saveOrUpdate(session, false);
	}

	/** 新增或更新实例，手动指定使用的Hibernate Session，并指定是否为逻辑删除 */
	public void saveOrUpdate(Session session, boolean logicalRemove) {
		String mode = getId() == null ? "新增" : "更新";
		if (!logicalRemove && getId() != null) {
			this.updateTime = new Date();
		}
		session.saveOrUpdate(this);
		if (!logicalRemove) {
			LOGGER.info("【{}】【{}】【{}】", mode, this.getClass().getSimpleName(), this);
		}
	}

	/** 物理删除实例，默认使用当前线程的Hibernate Session */
	public void remove() {
		remove(Environment.getSession());
	}

	/** 物理删除实例，手动指定使用的Hibernate Session */
	public void remove(Session session) {
		session.delete(this);
		LOGGER.info("【物理删除】【{}】【{}】", this.getClass().getSimpleName(), this);
	}

	@Override
	public String toString() {
		return EntityUtils.toString(this);
	}

	@Override
	public int hashCode() {
		return getId() == null ? System.identityHashCode(this) : getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && getId() != null && this.getClass().isInstance(obj) && getId().equals(this.getClass().cast(obj).getId());
	}
}