package cn.sini.cgb.api.cgb.query.group;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.sini.cgb.api.cgb.entity.group.Community;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.GroupType;
import cn.sini.cgb.api.cgb.entity.group.GroupOrder.ReviewStates;
import cn.sini.cgb.api.cgb.entity.group.GroupOrderState;
import cn.sini.cgb.api.cgb.entity.group.WeChatUser;
import cn.sini.cgb.common.entity.AbstractLogicalRemoveEntity;
import cn.sini.cgb.common.query.AbstractLogicalRemoveQuery;
import cn.sini.cgb.common.util.CommonUtils;

/**
 * 团单查询
 * 
 * @author gaowei
 */
public class GroupOrderQuery extends AbstractLogicalRemoveQuery {

	@Override
	protected Class<? extends AbstractLogicalRemoveEntity> queryEntity() {
		return GroupOrder.class;
	}

	/** 添加主题查询条件 */
	public GroupOrderQuery theme(String theme) {
		if (StringUtils.isNotEmpty(theme)) {
			addCriterion(Restrictions.like("theme", theme, MatchMode.ANYWHERE));
		}
		return this;
	}

	/** 添加小于等于结束时间查询条件 */
	public GroupOrderQuery endTimeLe(Date endTime) {
		if (endTime != null) {
			addCriterion(Restrictions.le("endTime", endTime));
		}
		return this;
	}

	/** 添加团单状态查询条件 */
	public GroupOrderQuery groupOrderState(GroupOrderState... groupOrderState) {
		Object[] values = CommonUtils.removeEmptyElement(groupOrderState);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("groupOrderState", values[0]));
			} else {
				addCriterion(Restrictions.in("groupOrderState", values));
			}
		}
		return this;
	}

	/** 添加团单状态sql查询条件 */
	public GroupOrderQuery groupOrderStateQuery(GroupOrderStateQuery groupOrderStateQuery) {
		if (groupOrderStateQuery != null) {
			addCriteria("groupOrderState", groupOrderStateQuery);
		}
		return this;
	}

	/** 添加团单状态sql查询条件 */
	public GroupOrderQuery shareTicketQuery(ShareTicketQuery shareTicketQuery) {
		if (shareTicketQuery != null) {
			addCriteria("shareTickets", shareTicketQuery);
		}
		return this;
	}

	/** 添加微信用户查询条件 */
	public GroupOrderQuery weChatUser(WeChatUser weChatUser) {
		if (weChatUser != null) {
			addCriterion(Restrictions.eq("weChatUser", weChatUser));
		}
		return this;
	}

	/** 添加微信用户链接查询条件 */
	public GroupOrderQuery weChatUserQuery(WeChatUserQuery weChatUserQuery) {
		if (weChatUserQuery != null) {
			addCriteria("weChatUser", weChatUserQuery);
		}
		return this;
	}

	/** 大于等于团单结束时间 */
	public GroupOrderQuery endTimeGe(Date endTime) {
		if (endTime != null) {
			addCriterion(Restrictions.ge("endTime", endTime));
		}
		return this;
	}

	/** 可提现时间小于当前时间 */
	public GroupOrderQuery cashWithdrawalTimeLe(Date cashWithdrawalTime) {
		if (cashWithdrawalTime != null) {
			addCriterion(Restrictions.le("cashWithdrawalTime", cashWithdrawalTime));
		}
		return this;
	}

	/** 添加订单号查询条件 */
	public GroupOrderQuery isFinish(Boolean isFinish) {
		addCriterion(Restrictions.eq("isFinish", isFinish));
		return this;
	}

	/** 添加社区查询条件 */
	public GroupOrderQuery community(Community... community) {
		Object[] values = CommonUtils.removeEmptyElement(community);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("community", values[0]));
			} else {
				addCriterion(Restrictions.in("community", values));
			}
		}
		return this;
	}

	/** 添加社区链接查询条件 */
	public GroupOrderQuery communityQuery(CommunityQuery communityQuery) {
		if (communityQuery != null) {
			addCriteria("community", communityQuery);
		}
		return this;
	}

	/** 添加大于发布时间查询条件（发布时间N天内，day参数应该是负数，如 3天前 = -3） */
	public GroupOrderQuery releaseTimeGe(Integer day) {
		if (day != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, day);
			addCriterion(Restrictions.ge("releaseTime", calendar.getTime()));
		}
		return this;
	}

	/** 添加团单审核状态查询条件 */
	public GroupOrderQuery reviewStates(ReviewStates... reviewStates) {
		Object[] values = CommonUtils.removeEmptyElement(reviewStates);
		if (ArrayUtils.isNotEmpty(values)) {
			if (values.length == 1) {
				addCriterion(Restrictions.eq("reviewStates", values[0]));
			} else {
				addCriterion(Restrictions.in("reviewStates", values));
			}
		}
		return this;
	}

	/** 添加置顶查询条件 */
	public GroupOrderQuery isTop(Boolean isTop) {
		if (isTop != null) {
			addCriterion(Restrictions.eq("isTop", isTop));
		}
		return this;
	}

	/** 添加拼团类型查询条件 */
	public GroupOrderQuery groupType(GroupType groupType) {
		if (groupType != null) {
			addCriterion(Restrictions.eq("groupType", groupType));
		}
		return this;
	}

	/** 添加 是否能升级 查询条件 */
	public GroupOrderQuery isUpgrade(Boolean isUpgrade) {
		if (isUpgrade != null) {
			addCriterion(Restrictions.eq("isUpgrade", isUpgrade));
		}
		return this;
	}

	/** 大于团单自提结束时间 */
	public GroupOrderQuery selfExtractingEndTimeLt(Date selfExtractingEndTime) {
		if (selfExtractingEndTime != null) {
			addCriterion(Restrictions.lt("selfExtractingEndTime", selfExtractingEndTime));
		}
		return this;
	}
}
