package cn.sini.cgb.api.cgb.query.group;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState.States;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 团单状态查询
 * 
 * @author gaowei
 */
public class GroupOrderStateQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return GroupOrderState.class;
	}

	/** 添加团单状态查询条件 */
	public GroupOrderStateQuery states(States... states) {
		Object[] values = CommonUtils.removeEmptyElement(states);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("states", values[0]));
			} else {
				addCriterion(Restrictions.in("states", values));
			}
		}
		return this;
	}
}
