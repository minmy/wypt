package cn.sini.cgb.api.cgb.query.pay;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.pay.RefundBill;
import cn.sini.cgb.api.cgb.entity.pay.RefundBill.RefundStatusEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class RefundBillQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return RefundBill.class;
	}
	/** 添加订单号查询条件 */
	public RefundBillQuery orderNumber(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.eq("orderNumber", orderNumber));
		}
		return this;
	}
	public RefundBillQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}
	public RefundBillQuery refundStatus(RefundStatusEnum rEnum) {
		addCriterion(Restrictions.eq("refundStatus", rEnum));
		return this;
	}
}
