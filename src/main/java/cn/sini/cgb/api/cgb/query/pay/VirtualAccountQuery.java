package cn.sini.cgb.api.cgb.query.pay;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.pay.VirtualAccount;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class VirtualAccountQuery extends AbstractLogicalRemoveQuery{

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return VirtualAccount.class;
	}
	/** 添加openId查询条件 */
	public VirtualAccountQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}

}
