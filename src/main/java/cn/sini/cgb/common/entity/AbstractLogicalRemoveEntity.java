package cn.sini.cgb.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.BooleanUtils;
import org.hibernate.Session;

import cn.sini.cgb.common.util.Environment;

/**
 * 抽象实体类-逻辑删除
 * 
 * @author 杨海彬
 */
@MappedSuperclass
public abstract class AbstractLogicalRemoveEntity extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	/** 是否删除 */
	@Column(name = "REMOVE", nullable = false)
	private Boolean remove = false;

	/** 删除时间 */
	@Column(name = "REMOVE_TIME")
	private Date removeTime;

	/**
	 * 获取是否删除
	 * 
	 * @return remove 是否删除
	 */
	public Boolean getRemove() {
		return remove;
	}

	/**
	 * 设置是否删除
	 * 
	 * @param remove 是否删除
	 */
	public void setRemove(Boolean remove) {
		this.remove = remove;
	}

	/**
	 * 获取删除时间
	 * 
	 * @return removeTime 删除时间
	 */
	public Date getRemoveTime() {
		return removeTime;
	}

	/**
	 * 设置删除时间
	 * 
	 * @param removeTime 删除时间
	 */
	public void setRemoveTime(Date removeTime) {
		this.removeTime = removeTime;
	}

	/** 是否已删除 */
	public boolean isRemove() {
		return BooleanUtils.isTrue(this.remove);
	}

	/** 逻辑删除实例，默认使用当前线程的Hibernate Session */
	@Override
	public void remove() {
		remove(Environment.getSession());
	}

	/** 逻辑删除实例，手动指定使用的Hibernate Session */
	@Override
	public void remove(Session session) {
		this.remove = true;
		this.removeTime = new Date();
		super.saveOrUpdate(session, true);
		LOGGER.info("【逻辑删除】【{}】【{}】", this.getClass().getSimpleName(), this);
	}
}