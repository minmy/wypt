package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodityBasic;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 商品品目查询类
 * 
 * @author gaowei
 */
public class GroupCommodityBasicQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return GroupCommodityBasic.class;
	}
	
	/** 添加所属团单查询条件 */
	public GroupCommodityBasicQuery groupOrderQuery(GroupOrderQuery groupOrderQuery) {
		if (groupOrderQuery != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrderQuery));
		}
		return this;
	}
	
	/** 添加所属团单查询条件 */
	public GroupCommodityBasicQuery groupOrder(GroupOrder groupOrder) {
		if (groupOrder != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrder));
		}
		return this;
	}

}
