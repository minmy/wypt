package cn.sini.cgb.api.cgb.query.group;

import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Label;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class LabelQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Label.class;
	}

	/** 添加上级标签为null查询条件 */
	public LabelQuery superLabelIsNull() {
		addCriterion(Restrictions.isNull("superLabel"));
		return this;
	}
}
