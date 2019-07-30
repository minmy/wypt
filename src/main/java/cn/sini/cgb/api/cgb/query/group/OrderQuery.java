package cn.sini.cgb.api.cgb.query.group;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.Order;
import cn.sini.cgb.api.cgb.entity.group.Order.PayState;
import cn.sini.cgb.api.cgb.entity.group.OrderState;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 订单查询
 *
 * @author gaowei
 */
public class OrderQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return Order.class;
	}

	/** 添加订单号查询条件 */
	public OrderQuery orderNumber(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.eq("orderNumber", orderNumber));
		}
		return this;
	}

	/** 添加订单随机6位数查询条件 */
	public OrderQuery randomNumber(String randomNumber) {
		if (StringUtils.isNotEmpty(randomNumber)) {
			addCriterion(Restrictions.eq("randomNumber", randomNumber));
		}
		return this;
	}

	/** 添加分享人openid查询条件 */
	public OrderQuery originOpenId(String originOpenId) {
		if (StringUtils.isNotEmpty(originOpenId)) {
			addCriterion(Restrictions.eq("originOpenId", originOpenId));
		}
		return this;
	}

	/** 添加分享人originOrderNumber查询条件 */
	public OrderQuery originOrderNumber(String originOrderNumber) {
		if (StringUtils.isNotEmpty(originOrderNumber)) {
			addCriterion(Restrictions.eq("originOrderNumber", originOrderNumber));
		}
		return this;
	}

	/** 添加用户查询条件 */
	public OrderQuery weChatUser(WeChatUser weChatUser) {
		if (weChatUser != null) {
			addCriterion(Restrictions.eq("weChatUser", weChatUser));
		}
		return this;
	}

	/** 添加订单状态查询条件 */
	public OrderQuery orderState(OrderState... orderState) {
		Object[] values = CommonUtils.removeEmptyElement(orderState);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("orderState", values[0]));
			} else {
				addCriterion(Restrictions.in("orderState", values));
			}
		}
		return this;
	}

	/** 添加订单状态语句查询条件 */
	public OrderQuery orderStateQuery(OrderStateQuery orderStateQuery) {
		if (orderStateQuery != null) {
			addCriteria("orderState", orderStateQuery);
		}
		return this;
	}

	/** 添加支付状态查询条件 */
	public OrderQuery payState(PayState... payState) {
		Object[] values = CommonUtils.removeEmptyElement(payState);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("payState", values[0]));
			} else {
				addCriterion(Restrictions.in("payState", values));
			}
		}
		return this;
	}

	/** 添加所属团单查询条件 */
	public OrderQuery groupOrder(GroupOrder groupOrder) {
		if (groupOrder != null) {
			addCriterion(Restrictions.eq("groupOrder", groupOrder));
		}
		return this;
	}

	/** 添加拼团链接查询条件 */
	public OrderQuery groupOrderQuery(GroupOrderQuery groupOrderQuery) {
		if (groupOrderQuery != null) {
			addCriteria("groupOrder", groupOrderQuery);
		}
		return this;
	}

	/** 添加小于等于创建时间查询条件 */
	public OrderQuery createTimeLe(Date createTime) {
		if (createTime != null) {
			addCriterion(Restrictions.le("createTime", createTime));
		}
		return this;
	}

	/** 添加大于等于创建时间查询条件 */
	public OrderQuery createTimeGe(Date createTime) {
		if (createTime != null) {
			addCriterion(Restrictions.ge("createTime", createTime));
		}
		return this;
	}

	/** 订单编号条件查询 */
	public OrderQuery orderNumberLike(String orderNumber) {
		if (StringUtils.isNotEmpty(orderNumber)) {
			addCriterion(Restrictions.ilike("orderNumber", orderNumber, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 分享随机码条件查询 */
	public OrderQuery shareRandomNumber(String shareRandomNumber) {
		if (StringUtils.isNotEmpty(shareRandomNumber)) {
			addCriterion(Restrictions.ge("shareRandomNumber", shareRandomNumber));
		}
		return this;
	}
}
