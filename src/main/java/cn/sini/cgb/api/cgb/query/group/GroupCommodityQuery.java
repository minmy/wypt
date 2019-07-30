package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.GroupCommodityBasic;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 团单商品查询
 * 
 * @author gaowei
 */
public class GroupCommodityQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return GroupCommodity.class;
	}

	/** 添加所属团单查询条件 */
	public GroupCommodityQuery groupOrder(GroupOrder groupOrder) {
		if (groupOrder != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrder));
		}
		return this;
	}

	/** 添加所属订单商品查询条件 */
	public GroupCommodityQuery id(Long id) {
		if (id != null) {
			addCriterion(Restrictions.eq("id", id));
		}
		return this;
	}

	/** 添加所属团单查询条件 */
	public GroupCommodityQuery groupCommodityBasicQuery(GroupCommodityBasicQuery groupCommodityBasicQuery) {
		if (groupCommodityBasicQuery != null) {
			addCriterion(Restrictions.eq("groupCommodityBasic", groupCommodityBasicQuery));
		}
		return this;
	}
	
	/** 添加所属团单查询条件 */
	public GroupCommodityQuery groupCommodityBasic(GroupCommodityBasic groupCommodityBasic) {
		if (groupCommodityBasic != null) {
			addCriterion(Restrictions.eq("groupCommodityBasic", groupCommodityBasic));
		}
		return this;
	}
}
