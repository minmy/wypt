package cn.sini.cgb.api.cgb.query.verification;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.entity.verification.Business.BusinessType;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 商户实体查询类
 *
 * @author lijianxin
 */
public class BusinessQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Business.class;
	}

	/** 商户编号查询条件 */
	public BusinessQuery mchId(String mchId) {
		if (StringUtils.isNotEmpty(mchId)) {
			addCriterion(Restrictions.eq("mchId", mchId));
		}
		return this;
	}

	/** 商户名称查询条件 */
	public BusinessQuery name(String name) {
		if (StringUtils.isNotEmpty(name)) {
			addCriterion(Restrictions.like("name", name, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 商户类型查询条件 */
	public BusinessQuery businessType(BusinessType businessType) {
		if (businessType != null) {
			addCriterion(Restrictions.eq("businessType", businessType));
		}
		return this;
	}
}
