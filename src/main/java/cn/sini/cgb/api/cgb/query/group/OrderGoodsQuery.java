package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupCommodity;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.OrderGoods;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 订单商品查询
 * 
 * @author gaowei
 */
public class OrderGoodsQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return OrderGoods.class;
	}

	/** 添加团单商品查询条件 */
	public OrderGoodsQuery groupCommodity(GroupCommodity groupCommodity) {
		if (groupCommodity != null) {
			addCriterion(Restrictions.eq("groupCommodity", groupCommodity));
		}
		return this;
	}

	/** 添加团单商品查询条件 */
	public OrderGoodsQuery order(Order order) {
		if (order != null) {
			addCriterion(Restrictions.eq("order", order));
		}
		return this;
	}
}
