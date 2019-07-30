package cn.sini.cgb.api.cgb.entity.statistics;

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

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;


/**
 * 团单统计实体,团单访问记录
 * 
 * @author 黎嘉权
 */
@Entity
@Table(name = StaGroupOrder.TABLE_NAME)
public class StaGroupOrder extends AbstractLogicalRemoveEntity {
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "T_STATISTICS_GROUP_ORDER";
	
	/** ID */
	@Id
	@TableGenerator(name = TABLE_NAME, table = SEQUENCE_TABLE, pkColumnValue = TABLE_NAME)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@Column(name = "ID")
	private Long id;
	
	/** 昵称 */
	@Column(name = "NAME")
	private String name;
	
	/** OpenId */
	@Column(name = "OPEN_ID", nullable = false)
	private String openId;
	
	/** 所属团单 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FK_GROUP_ORDER", nullable = false)
	private GroupOrder groupOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public GroupOrder getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(GroupOrder groupOrder) {
		this.groupOrder = groupOrder;
	}

	
}
