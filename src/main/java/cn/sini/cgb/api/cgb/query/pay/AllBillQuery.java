package cn.sini.cgb.api.cgb.query.pay;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.api.cgb.entity.pay.AllBill;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.BillTypeEnum;
import cn.sini.cgb.api.cgb.entity.pay.AllBill.CashTypeEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

public class AllBillQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return AllBill.class;
	}
	public AllBillQuery weChatUser(WeChatUser weChatUser) {
		addCriterion(Restrictions.eq("weChatUser", weChatUser));
		return this;
	}
	
	public AllBillQuery flag(CashTypeEnum flag) {
			addCriterion(Restrictions.eq("flag", flag));
		return this;
	}
	/** 添加订单号查询条件 */
	public AllBillQuery applyNumber(String applyNumber) {
		if (StringUtils.isNotEmpty(applyNumber)) {
			addCriterion(Restrictions.eq("applyNumber", applyNumber));
		}
		return this;
	}
	/** 添加提现号查询条件 */
	public AllBillQuery orderNumber(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.eq("orderNumber", orderNumber));
		}
		return this;
	}
	/** 添加团单号数组查询条件 */
	public AllBillQuery groupId(String... groupId) {
		Object[] values = CommonUtils.removeEmptyElement(groupId);
		if (ArrayUtils.isNotEmpty(values)) {
			addCriterion(Restrictions.in("groupId", values));
		}
		return this;
	}
	/** 添加团单号查询条件 */
	public AllBillQuery groupId(String groupId) {
		addCriterion(Restrictions.eq("groupId", groupId));
		return this;
	}
	/** 添加订单状态查询条件 */
	public AllBillQuery billType(BillTypeEnum... billTypeEnum) {
		Object[] values = CommonUtils.removeEmptyElement(billTypeEnum);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("billType", values[0]));
			} else {
				addCriterion(Restrictions.in("billType", values));
			}
		}
		return this;
	}
}
