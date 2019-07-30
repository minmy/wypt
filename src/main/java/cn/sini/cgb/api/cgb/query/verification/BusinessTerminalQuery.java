package cn.sini.cgb.api.cgb.query.verification;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.verification.Business;
import cn.sini.cgb.api.cgb.entity.verification.BusinessTerminal;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

/**
 * 商家终端(商家白名单扫描器)实体查询类
 *
 * @author lijianxin
 */
public class BusinessTerminalQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return BusinessTerminal.class;
	}

	/** pos终端ID查询条件 */
	public BusinessTerminalQuery posId(String posId) {
		if (StringUtils.isNotEmpty(posId)) {
			addCriterion(Restrictions.eq("posId", posId));
		}
		return this;
	}

	/** 添加所属商家查询条件 */
	public BusinessTerminalQuery business(Business business) {
		if (business != null) {
			addCriterion(Restrictions.eq("business", business));
		}
		return this;
	}

	/** 添加所属商家连接查询条件 */
	public BusinessTerminalQuery businessQuery(BusinessQuery businessQuery) {
		if (businessQuery != null) {
			addCriteria("business", businessQuery);
		}
		return this;
	}
}
