package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.OrderState.OrderStates;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 订单状态查询
 * 
 * @author gaowei
 */
public class OrderStateQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return OrderState.class;
	}

	/** 添加订单状态查询条件 */
	public OrderStateQuery orderStates(OrderStates... orderStates) {
		Object[] values = CommonUtils.removeEmptyElement(orderStates);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("orderStates", values[0]));
			} else {
				addCriterion(Restrictions.in("orderStates", values));
			}
		}
		return this;
	}

	/** 添加过滤订单状态查询条件 */
	public OrderStateQuery orderStatesNe(OrderStates orderStates) {
		if (orderStates != null) {
			addCriterion(Restrictions.ne("orderStates", orderStates));
		}
		return this;
	}
}
