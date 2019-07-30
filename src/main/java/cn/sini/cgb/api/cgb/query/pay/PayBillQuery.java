package cn.sini.cgb.api.cgb.query.pay;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.pay.PayBill;
import cn.sini.cgb.api.cgb.entity.pay.PayBill.PayStatusEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;

public class PayBillQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return PayBill.class;
	}
	/** 添加订单号查询条件 */
	public PayBillQuery orderNumber(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.eq("orderNumber", orderNumber));
		}
		return this;
	}
	public PayBillQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}
	public PayBillQuery payStatus(PayStatusEnum payStatus) {
		addCriterion(Restrictions.eq("payStatus", payStatus));
		return this;
	}
}
