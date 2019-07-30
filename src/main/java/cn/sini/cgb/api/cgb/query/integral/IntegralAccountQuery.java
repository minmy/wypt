package cn.sini.cgb.api.cgb.query.integral;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.integral.IntegralAccount;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class IntegralAccountQuery extends AbstractLogicalRemoveQuery{
	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return IntegralAccount.class;
	}
	/** 添加openId查询条件 */
	public IntegralAccountQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}
}
