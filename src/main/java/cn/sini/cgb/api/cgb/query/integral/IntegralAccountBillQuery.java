package cn.sini.cgb.api.cgb.query.integral;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralSourceTypeEnum;
import cn.sini.cgb.api.cgb.entity.integral.IntegralAccountBill.IntegralTypeEnum;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 积分消费查询
 * 
 * @author 黎嘉权
 */
public class IntegralAccountBillQuery extends AbstractLogicalRemoveQuery{
	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return IntegralAccountBill.class;
	}
	/** 添加订单号查询条件 */
	public IntegralAccountBillQuery orderNumber(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.eq("orderNumber", orderNumber));
		}
		return this;
	}
	/** 添加团单号查询条件 */
	public IntegralAccountBillQuery groupId(Long groupId) {
		
		addCriterion(Restrictions.eq("groupId", groupId));
		return this;
	}
	/** 添加商品号查询条件 */
	public IntegralAccountBillQuery groupCommodityId(Long groupCommodityId) {
		
		addCriterion(Restrictions.eq("groupCommodityId", groupCommodityId));
		return this;
	}
	/** 添加openId查询条件 */
	public IntegralAccountBillQuery openId(String openId) {
		if (StringUtils.isNotEmpty(openId)) {
			addCriterion(Restrictions.eq("openId", openId));
		}
		return this;
	}
	/** 添加integralNumber查询条件 */
	public IntegralAccountBillQuery integralNumber(String integralNumber) {
		if (StringUtils.isNotEmpty(integralNumber)) {
			addCriterion(Restrictions.eq("integralNumber", integralNumber));
		}
		return this;
	}
	
	/** 账户加减操作类型查询条件 */
	public IntegralAccountBillQuery integralType(IntegralTypeEnum... integralType) {
		Object[] values = CommonUtils.removeEmptyElement(integralType);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("integralType", values[0]));
			} else {
				addCriterion(Restrictions.in("integralType", values));
			}
		}
		return this;
	}
	
	/** 账户来源类型查询条件 */
	public IntegralAccountBillQuery integralSourceType(IntegralSourceTypeEnum... integralSourceTypeEnum) {
		Object[] values = CommonUtils.removeEmptyElement(integralSourceTypeEnum);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("integralSourceTypeEnum", values[0]));
			} else {
				addCriterion(Restrictions.in("integralSourceTypeEnum", values));
			}
		}
		return this;
	}
	
	/** 添加小于等于有效时间查询条件 */
	public IntegralAccountBillQuery validDate(Date validDate) {
		if (validDate != null) {
			addCriterion(Restrictions.le("validDate", validDate));
		}
		return this;
	}
}
